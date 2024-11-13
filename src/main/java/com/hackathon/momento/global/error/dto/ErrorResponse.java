package com.hackathon.momento.global.error.dto;

public record ErrorResponse(
        int statusCode,
        String message
) {
}
