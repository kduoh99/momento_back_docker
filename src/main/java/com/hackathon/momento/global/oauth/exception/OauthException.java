package com.hackathon.momento.global.oauth.exception;

import com.hackathon.momento.global.error.exception.AuthGroupException;

public class OauthException extends AuthGroupException {
    public OauthException(String message) {
        super(message);
    }

    public OauthException() {
        this("카카오 서버와의 통신 과정에서 문제가 발생했습니다.");
    }
}
