package com.hackathon.momento.member.domain;

import com.hackathon.momento.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(length = 1024)
    private String stack;

    @Column(length = 1024)
    private String persona;

    private String ability;

    private boolean isFirstLogin = true;

    @Enumerated(EnumType.STRING)
    private RoleType roleType;

    @Builder
    private Member(String email, String name, String stack, String persona, String ability, RoleType roleType) {
        this.email = email;
        this.name = name;
        this.stack = stack;
        this.persona = persona;
        this.ability = ability;
        this.roleType = roleType;
    }

    public void completeProfile(String stack, String persona, String ability) {
        this.stack = stack;
        this.persona = persona;
        this.ability = ability;
        this.isFirstLogin = false;
    }

    public void updateProfile(String name, String stack, String persona, String ability) {
        this.name = name;
        this.stack = stack;
        this.persona = persona;
        this.ability = ability;
    }
}
