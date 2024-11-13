package com.hackathon.momento.auth.application;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class TokenRenewService {

    private static final String REFRESH_TOKEN_PREFIX = "refreshToken:";

    private final RedisTemplate<String, String> redisTemplate;

    @Transactional
    public void saveRefreshToken(String refreshToken, Long memberId) {
        deleteExistingToken(memberId);

        String key = REFRESH_TOKEN_PREFIX + memberId;
        redisTemplate.opsForValue().set(key, refreshToken);
    }

    private void deleteExistingToken(Long memberId) {
        String key = REFRESH_TOKEN_PREFIX + memberId;
        redisTemplate.delete(key);
    }
}