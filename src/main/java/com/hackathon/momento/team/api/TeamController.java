package com.hackathon.momento.team.api;

import com.hackathon.momento.global.template.RspTemplate;
import com.hackathon.momento.team.api.dto.request.TeamBuildingReqDto;
import com.hackathon.momento.team.api.dto.response.TeamInfoResDto;
import com.hackathon.momento.team.application.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/team")
@Tag(name = "팀", description = "팀을 담당하는 API 그룹")
public class TeamController {

    private final TeamService teamService;

    @PostMapping("/building")
    @Operation(
            summary = "팀 빌딩 정보 저장",
            description = "사용자가 팀 빌딩을 위해 필요한 정보들을 입력합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "팀 빌딩 정보 저장 성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    public RspTemplate<Void> saveTeamBuilding(
            Principal principal,
            @Valid @RequestBody TeamBuildingReqDto reqDto) {

        teamService.saveTeamBuilding(principal, reqDto);
        return new RspTemplate<>(HttpStatus.CREATED, "팀 빌딩 정보 저장 성공");
    }

    @PostMapping("/execute-building")
    @Operation(
            summary = "AI 팀 빌딩 요청 (테스트용 수동 요청)",
            description = "각 사용자의 특성을 고려하여 최적의 팀을 구성합니다. 매일 오후 2시에 자동 실행되지만, 테스트를 위해 수동으로도 요청할 수 있습니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "AI 팀 빌딩 요청 성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    public RspTemplate<Void> executeTeamBuilding() {

        teamService.executeTeamBuilding();
        return new RspTemplate<>(HttpStatus.OK, "AI 팀 빌딩 요청 성공");
    }

    @GetMapping("/completed-profile")
    @Operation(
            summary = "완성된 팀 정보 조회",
            description = "사용자의 완성된 팀 정보를 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "팀 정보 조회 성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    public RspTemplate<List<TeamInfoResDto>> getTeamInfoProfile(Principal principal) {
        List<TeamInfoResDto> teamProfile = teamService.getTeamInfoProfile(principal);
        return new RspTemplate<>(HttpStatus.OK, "팀 정보 조회 성공", teamProfile);
    }

    @PostMapping("/check-duplicate")
    @Operation(
            summary = "AI 팀 빌딩 요청 중복 검사",
            description = "사용자의 AI 팀 빌딩 요청이 존재하는지 여부를 검사합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "중복 검사 성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    public RspTemplate<String> checkDuplicateRequest(Principal principal) {
        boolean isDuplicate = teamService.checkDuplicate(principal);
        return new RspTemplate<>(HttpStatus.OK, String.valueOf(isDuplicate));
    }
}
