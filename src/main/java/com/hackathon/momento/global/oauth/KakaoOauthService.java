package com.hackathon.momento.global.oauth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackathon.momento.auth.api.dto.response.AuthResDto;
import com.hackathon.momento.auth.application.TokenRenewService;
import com.hackathon.momento.global.jwt.TokenProvider;
import com.hackathon.momento.global.oauth.exception.OauthException;
import com.hackathon.momento.member.domain.Member;
import com.hackathon.momento.member.domain.RoleType;
import com.hackathon.momento.member.domain.repository.MemberRepository;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class KakaoOauthService {

    @Value("${oauth.kakao.client-id}")
    private String KAKAO_CLIENT_ID;

    @Value("${oauth.kakao.redirect-uri}")
    private String KAKAO_REDIRECT_URI;

    @Value("${oauth.kakao.token-url}")
    private String KAKAO_TOKEN_URL;

    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;
    private final TokenRenewService tokenRenewService;
    private final ObjectMapper objectMapper;

    @Transactional
    public AuthResDto signUpOrLogin(String code) {
        try {
            String kakaoAccessToken = getKakaoAccessToken(code);
            String[] memberInfo = getMemberInfo(kakaoAccessToken);

            Member member = memberRepository.findByEmail(memberInfo[0])
                    .orElseGet(() -> memberRepository.save(Member.builder()
                            .email(memberInfo[0])
                            .name(memberInfo[1])
                            .roleType(RoleType.ROLE_USER)
                            .build())
                    );

            String accessToken = tokenProvider.createAccessToken(member);
            String refreshToken = tokenProvider.createRefreshToken(member);
            tokenRenewService.saveRefreshToken(refreshToken, member.getId());

            return AuthResDto.of(accessToken, refreshToken);
        } catch (JsonProcessingException e) {
            throw new OauthException();
        }
    }

    private String getKakaoAccessToken(String code) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();

        String url = UriComponentsBuilder.fromHttpUrl(KAKAO_TOKEN_URL)
                .queryParam("grant_type", "authorization_code")
                .queryParam("client_id", KAKAO_CLIENT_ID)
                .queryParam("redirect_uri", KAKAO_REDIRECT_URI)
                .queryParam("code", code)
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> reqEntity = new HttpEntity<>(headers);
        ResponseEntity<String> resEntity = restTemplate.exchange(url, HttpMethod.POST, reqEntity, String.class);

        if (!resEntity.getStatusCode().is2xxSuccessful()) {
            throw new OauthException();
        }

        String json = resEntity.getBody();
        JsonNode jsonNode = objectMapper.readTree(json);

        return jsonNode.get("access_token").asText();
    }

    private String[] getMemberInfo(String kakaoAccessToken) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://kapi.kakao.com/v2/user/me?access_token=" + kakaoAccessToken;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + kakaoAccessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        RequestEntity<Void> reqEntity = new RequestEntity<>(headers, HttpMethod.GET, URI.create(url));
        ResponseEntity<String> resEntity = restTemplate.exchange(reqEntity, String.class);

        if (!resEntity.getStatusCode().is2xxSuccessful()) {
            throw new OauthException();
        }

        JsonNode jsonNode = objectMapper.readTree(resEntity.getBody());
        String email = jsonNode.path("kakao_account").path("email").asText();
        String name = jsonNode.path("kakao_account").path("profile").path("nickname").asText();

        return new String[]{email, name};
    }
}
