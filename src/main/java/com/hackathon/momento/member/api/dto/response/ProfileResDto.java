package com.hackathon.momento.member.api.dto.response;

import com.hackathon.momento.member.domain.Member;
import lombok.Builder;

@Builder
public record ProfileResDto(
        String email,
        String name,
        String stack,
        String persona,
        String ability
) {
    public static ProfileResDto from(Member member) {
        return ProfileResDto.builder()
                .email(member.getEmail())
                .name(member.getName())
                .stack(member.getStack())
                .persona(member.getPersona())
                .ability(member.getAbility())
                .build();
    }
}
