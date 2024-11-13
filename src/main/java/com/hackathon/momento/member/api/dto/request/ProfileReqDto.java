package com.hackathon.momento.member.api.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ProfileReqDto(
        @NotBlank(message = "스택은 필수 입력 항목입니다")
        String stack,

        @NotBlank(message = "성격은 필수 입력 항목입니다")
        String persona,

        @NotBlank(message = "역량은 필수 입력 항목입니다")
        String ability
) {
}
