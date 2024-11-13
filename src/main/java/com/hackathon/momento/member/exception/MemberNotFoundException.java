package com.hackathon.momento.member.exception;

import com.hackathon.momento.global.error.exception.NotFoundGroupException;

public class MemberNotFoundException extends NotFoundGroupException {
    public MemberNotFoundException(String message) {
        super(message);
    }

    public MemberNotFoundException() {
        this("존재하지 않는 사용자 입니다.");
    }
}
