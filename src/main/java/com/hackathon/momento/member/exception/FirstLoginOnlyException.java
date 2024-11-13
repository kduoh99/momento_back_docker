package com.hackathon.momento.member.exception;

import com.hackathon.momento.global.error.exception.InvalidGroupException;

public class FirstLoginOnlyException extends InvalidGroupException {
    public FirstLoginOnlyException(String message) {
        super(message);
    }

    public FirstLoginOnlyException() {
        this("최초 로그인 시에만 설정할 수 있습니다.");
    }
}
