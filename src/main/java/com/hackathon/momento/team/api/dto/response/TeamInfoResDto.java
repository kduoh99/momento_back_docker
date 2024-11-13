package com.hackathon.momento.team.api.dto.response;

import com.hackathon.momento.team.domain.TeamInfo;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;

@Builder
public record TeamInfoResDto(
        String teamName,
        List<MemberInfoResDto> members
) {
    public static TeamInfoResDto from(TeamInfo teamInfo) {
        return TeamInfoResDto.builder()
                .teamName(teamInfo.getTeamName())
                .members(teamInfo.getTeamBuildings().stream()
                        .map(MemberInfoResDto::from)
                        .collect(Collectors.toList()))
                .build();
    }
}
