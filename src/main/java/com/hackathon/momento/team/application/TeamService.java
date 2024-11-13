package com.hackathon.momento.team.application;

import com.hackathon.momento.member.domain.Member;
import com.hackathon.momento.member.domain.repository.MemberRepository;
import com.hackathon.momento.member.exception.MemberNotFoundException;
import com.hackathon.momento.team.api.dto.Message;
import com.hackathon.momento.team.api.dto.request.GPTReqDto;
import com.hackathon.momento.team.api.dto.request.TeamBuildingReqDto;
import com.hackathon.momento.team.api.dto.response.GPTResDto;
import com.hackathon.momento.team.api.dto.response.TeamInfoResDto;
import com.hackathon.momento.team.domain.Status;
import com.hackathon.momento.team.domain.TeamBuilding;
import com.hackathon.momento.team.domain.TeamInfo;
import com.hackathon.momento.team.domain.repository.TeamBuildingRepository;
import com.hackathon.momento.team.domain.repository.TeamInfoRepository;
import com.hackathon.momento.team.exception.TeamBuildingConflictException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TeamService {

    private final TeamBuildingRepository teamBuildingRepository;
    private final TeamInfoRepository teamInfoRepository;
    private final MemberRepository memberRepository;
    private final RestTemplate restTemplate;
    private final EmailService emailService;

    @Value("${openai.api.url}")
    private String apiURL;

    @Value("${openai.model}")
    private String model;

    @Value("${openai.prompt}")
    private String promptTemplate;

    @Transactional
    public void saveTeamBuilding(Principal principal, TeamBuildingReqDto reqDto) {
        Member member = getMemberByPrincipal(principal);

        if (teamBuildingRepository.existsByMemberAndStatus(member, Status.PENDING)) {
            throw new TeamBuildingConflictException();
        }

        teamBuildingRepository.save(reqDto.toEntity(member));
    }

    @Scheduled(cron = "0 0 14 * * *")
    @Transactional
    public void executeTeamBuilding() {
        List<TeamBuilding> requests = teamBuildingRepository.findByStatus(Status.PENDING);
        if (requests.size() < 2) {
            return;
        }

        List<Message> messages = List.of(new Message("user", buildPrompt(requests)));
        GPTReqDto reqDto = new GPTReqDto(model, messages);
        log.info("Sending: {}", reqDto);
        GPTResDto resDto = restTemplate.postForObject(apiURL, reqDto, GPTResDto.class);

        if (resDto != null && !resDto.choices().isEmpty()) {
            String resContent = resDto.choices().get(0).message().content();
            log.info("Content: {}", resContent);

            List<TeamInfo> teamInfos = parseResToTeamInfo(resContent, requests);
            teamInfoRepository.saveAll(teamInfos);

            sendTeamCompletionEmails(teamInfos);
        }
    }

    private void sendTeamCompletionEmails(List<TeamInfo> teamInfos) {
        for (TeamInfo teamInfo : teamInfos) {
            String teamName = teamInfo.getTeamName();
            String description = teamInfo.getDescription();
            List<TeamBuilding> teamMembers = teamInfo.getTeamBuildings();

            StringBuilder emailBody = new StringBuilder();
            emailBody.append("<div style='font-family: Arial, sans-serif; max-width: 600px; margin: auto;'>")
                    .append("<h1 style='background-color: #4CAF50; color: white; padding: 10px; text-align: center;'>")
                    .append(teamName).append("</h1>")
                    .append("<p style='font-size: 16px; line-height: 1.5;'>").append(description).append("</p>")
                    .append("<h2 style='color: #333; margin-top: 20px;'>Team Members</h2>")
                    .append("<ul style='list-style-type: none; padding: 0;'>");

            for (TeamBuilding memberBuilding : teamMembers) {
                Member member = memberBuilding.getMember();
                emailBody.append(
                                "<li style='background-color: #f9f9f9; margin: 5px 0; padding: 10px; border-radius: 5px;'>")
                        .append("<strong>Name:</strong> ").append(member.getName()).append("<br>")
                        .append("<strong>Email:</strong> ").append(member.getEmail()).append("<br>")
                        .append("<strong>Position:</strong> ").append(memberBuilding.getMyPosition())
                        .append("</li>");
            }
            emailBody.append("</ul>")
                    .append("<p style='text-align: center; margin-top: 30px;'>")
                    .append("Thank you for using <strong>TeamUp</strong>")
                    .append("</p>")
                    .append("</div>");

            for (TeamBuilding memberBuilding : teamMembers) {
                emailService.sendMessage(memberBuilding.getMember().getEmail(), emailBody.toString());
            }
        }
    }

    private String buildPrompt(List<TeamBuilding> requests) {
        StringBuilder prompt = new StringBuilder("팀 빌딩 요청입니다. 사용자의 정보와 요구 사항을 바탕으로 최적의 팀을 만들어 주세요.\n");

        for (TeamBuilding request : requests) {
            Member member = request.getMember();
            prompt.append("이름: ").append(member.getName())
                    .append(", 이메일: ").append(member.getEmail())
                    .append(", 성격: ").append(member.getPersona())
                    .append(", 역량: ").append(member.getAbility())
                    .append(", 프로젝트 시작 날짜: ").append(request.getStartDate())
                    .append(", 종료 날짜: ").append(request.getEndDate())
                    .append(", 팀 사이즈: ").append(request.getTeamSize())
                    .append(", 본인 포지션: ").append(request.getMyPosition())
                    .append(", 요구 포지션 조합: ").append(request.getPositionCombination())
                    .append("\n");
        }

        prompt.append(promptTemplate);
        return prompt.toString();
    }

    private List<TeamInfo> parseResToTeamInfo(String resContent, List<TeamBuilding> requests) {
        String[] lines = resContent.split("\n");
        List<TeamInfo> teamInfos = new ArrayList<>();
        TeamInfo currentTeam = null;

        for (String line : lines) {
            line = line.trim();

            if (line.equals("- 팀 시작 -")) {
                if (currentTeam != null) {
                    teamInfos.add(currentTeam);
                }
                currentTeam = TeamInfo.builder().build();

            } else if (line.startsWith("팀 이름: ")) {
                String teamName = line.replace("팀 이름: ", "").trim();
                if (currentTeam != null) {
                    currentTeam = TeamInfo.builder()
                            .teamName(teamName)
                            .build();
                }

            } else if (line.startsWith("팀 설명: ")) {
                String description = line.replace("팀 설명: ", "").trim();
                if (currentTeam != null) {
                    currentTeam = TeamInfo.builder()
                            .teamName(currentTeam.getTeamName())
                            .description(description)
                            .build();
                }

            } else if (line.startsWith("- 이메일: ")) {
                String email = line.replace("- 이메일: ", "").trim();
                if (currentTeam != null) {
                    for (TeamBuilding request : requests) {
                        if (request.getMember().getEmail().equals(email)) {
                            currentTeam.addTeamBuilding(request);
                            request.updateStatus(Status.COMPLETED);
                        }
                    }
                }

            } else if (line.equals("- 팀 끝 -")) {
                if (currentTeam != null) {
                    teamInfos.add(currentTeam);
                    currentTeam = null;
                }
            }
        }

        if (currentTeam != null) {
            teamInfos.add(currentTeam);
        }

        return teamInfos;
    }

    public List<TeamInfoResDto> getTeamInfoProfile(Principal principal) {
        Member member = getMemberByPrincipal(principal);
        List<TeamBuilding> completedTeams = teamBuildingRepository.findByMemberAndStatus(member,
                Status.COMPLETED);

        return completedTeams.stream()
                .map(TeamBuilding::getTeamInfo)
                .map(TeamInfoResDto::from)
                .collect(Collectors.toList());
    }

    public boolean checkDuplicate(Principal principal) {
        Member member = getMemberByPrincipal(principal);
        return teamBuildingRepository.existsByMemberAndStatus(member, Status.PENDING);
    }

    private Member getMemberByPrincipal(Principal principal) {
        Long memberId = Long.parseLong(principal.getName());
        return memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);
    }
}