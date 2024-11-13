package com.hackathon.momento.team.domain;

import com.hackathon.momento.global.entity.BaseEntity;
import com.hackathon.momento.member.domain.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TeamBuilding extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_building_id")
    private Long id;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private int teamSize;

    @Column(nullable = false)
    private String myPosition;

    @Column(nullable = false, length = 1024)
    private String positionCombination;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_info_id")
    private TeamInfo teamInfo;

    @Builder
    private TeamBuilding(LocalDate startDate, LocalDate endDate, int teamSize, String myPosition,
                         String positionCombination, Status status, Member member) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.teamSize = teamSize;
        this.myPosition = myPosition;
        this.positionCombination = positionCombination;
        this.status = status;
        this.member = member;
    }

    protected void setTeamInfo(TeamInfo teamInfo) {
        this.teamInfo = teamInfo;
    }

    public void updateStatus(Status status) {
        this.status = status;
    }
}
