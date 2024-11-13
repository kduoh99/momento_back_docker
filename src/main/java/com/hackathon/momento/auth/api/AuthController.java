package com.hackathon.momento.auth.api;

import com.hackathon.momento.auth.api.dto.response.AuthResDto;
import com.hackathon.momento.global.oauth.KakaoOauthService;
import com.hackathon.momento.global.template.RspTemplate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Tag(name = "회원가입/로그인", description = "회원가입/로그인을 담당하는 API 그룹")
public class AuthController {

    private final KakaoOauthService kakaoOAuthService;

    @GetMapping("/callback")
    @Operation(
            summary = "카카오 회원가입/로그인 콜백",
            description = "카카오 로그인 후 리다이렉션된 URI입니다. 인가 코드를 받아서 accessToken을 요청하고, 회원가입 또는 로그인을 처리합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "회원가입/로그인 성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "500", description = "관리자 문의")
            }
    )
    public RspTemplate<AuthResDto> kakaoCallback(@RequestParam(name = "code") String code) {
        AuthResDto token = kakaoOAuthService.signUpOrLogin(code);
        return new RspTemplate<>(HttpStatus.OK, "토큰 발급", token);
    }
}
