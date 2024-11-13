package com.hackathon.momento.global.error.exception;

public abstract class ConflictGroupException extends RuntimeException {
    public ConflictGroupException(String message) {
        super(message);
    }
}
