package com.hackathon.momento.team.api.dto.request;

import com.hackathon.momento.member.domain.Member;
import com.hackathon.momento.team.domain.Status;
import com.hackathon.momento.team.domain.TeamBuilding;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

public record TeamBuildingReqDto(
        @NotNull(message = "프로젝트 시작 날짜는 필수 입력 항목입니다")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate startDate,

        @NotNull(message = "프로젝트 종료 날짜는 필수 입력 항목입니다")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate endDate,

        @Min(value = 2, message = "팀 인원 수는 최소 2명이어야 합니다")
        @Max(value = 6, message = "팀 인원 수는 최대 6명이어야 합니다")
        int teamSize,

        @NotBlank(message = "본인 포지션은 필수 입력 항목입니다")
        String myPosition,

        @NotBlank(message = "요구 포지션 조합은 필수 입력 항목입니다")
        String positionCombination
) {
    public TeamBuilding toEntity(Member member) {
        return TeamBuilding.builder()
                .startDate(this.startDate)
                .endDate(this.endDate)
                .teamSize(this.teamSize)
                .myPosition(this.myPosition)
                .positionCombination(this.positionCombination)
                .status(Status.PENDING)
                .member(member)
                .build();
    }
}
