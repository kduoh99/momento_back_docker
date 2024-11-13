package com.hackathon.momento.team.api.dto.response;

import com.hackathon.momento.team.api.dto.Message;
import java.util.List;

public record GPTResDto(
        List<Choice> choices,
        Usage usage
) {
    public record Choice(
            Message message
    ) {
    }

    public record Usage(
            int prompt_tokens,
            int completion_tokens,
            int total_tokens
    ) {
    }
}
