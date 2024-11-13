package com.hackathon.momento.team.api.dto.response;

import com.hackathon.momento.team.domain.TeamBuilding;
import lombok.Builder;

@Builder
public record MemberInfoResDto(
        String name,
        String email,
        String position
) {
    public static MemberInfoResDto from(TeamBuilding teamBuilding) {
        return MemberInfoResDto.builder()
                .name(teamBuilding.getMember().getName())
                .email(teamBuilding.getMember().getEmail())
                .position(teamBuilding.getMyPosition())
                .build();
    }
}
