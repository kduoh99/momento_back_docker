package com.hackathon.momento.team.exception;

import com.hackathon.momento.global.error.exception.ConflictGroupException;

public class TeamBuildingConflictException extends ConflictGroupException {
    public TeamBuildingConflictException(String message) {
        super(message);
    }

    public TeamBuildingConflictException() {
        this("이미 진행 중인 팀 빌딩 요청이 있습니다.");
    }
}
