package com.hackathon.momento.team.api.dto.request;

import com.hackathon.momento.team.api.dto.Message;
import java.util.List;

public record GPTReqDto(
        String model,
        List<Message> messages
) {
}
