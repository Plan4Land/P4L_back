package com.SpringBoot.Plan4Land.Controller;

import com.SpringBoot.Plan4Land.DTO.MemberResDto;
import com.SpringBoot.Plan4Land.DTO.MemberReqDto;
import com.SpringBoot.Plan4Land.DTO.TokenDto;
import com.SpringBoot.Plan4Land.JWT.JwtFilter;
import com.SpringBoot.Plan4Land.Service.AuthService;
import com.SpringBoot.Plan4Land.Service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final TokenService tokenService;

    // 유저 존재 확인
    @GetMapping("/exists/{userId}")
    public ResponseEntity<Boolean> isUser(@PathVariable String userId) {
        boolean isTrue = authService.isMember(userId);
        return ResponseEntity.ok(!isTrue);
    }

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<MemberResDto> signup(@RequestBody MemberReqDto memberReqDto) {
        return ResponseEntity.ok(authService.signUp(memberReqDto));
    }
    
    // 로그인
    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@RequestBody MemberReqDto memberReqDto) {
        return ResponseEntity.ok(authService.login(memberReqDto));
    }

    // 네이버 로그인
    private static final String NAVER_OAUTH_URL = "https://nid.naver.com/oauth2.0/token";
    @PostMapping("/naver/token")
    public ResponseEntity<String> getNaverToken(@RequestBody Map<String, String> requestBody) {
        String code = requestBody.get("code");
        String state = requestBody.get("state");

        String clientId = "fkmRtBoM0k0qYhHJIVRE";
        String clientSecret = "Ip5wT2637u";

        // 요청 파라미터 설정
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(NAVER_OAUTH_URL)
                .queryParam("grant_type", "authorization_code")
                .queryParam("client_id", clientId)
                .queryParam("client_secret", clientSecret)
                .queryParam("code", code)
                .queryParam("state", state);

        // RestTemplate을 사용하여 Naver API 요청
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.POST, entity, String.class);
        return ResponseEntity.ok(response.getBody());
    }
    @PostMapping("/naver/userinfo")
    public ResponseEntity<String> getNaverUserInfo(@RequestBody Map<String, String> requestBody) {
        String accessToken = requestBody.get("access_token");

        // 네이버 API 요청
        String naverApiUrl = "https://openapi.naver.com/v1/nid/me";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    naverApiUrl, HttpMethod.GET, entity, String.class
            );
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("네이버 사용자 정보 조회 실패");
        }
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<TokenDto> logout(@RequestBody MemberReqDto memberReqDto) {
        authService.logout(memberReqDto);
        return ResponseEntity.noContent().build();
    }

    // 이메일로 유저 탈퇴 확인
    @PostMapping("/isActivate/byEmail")
    public String isActivateById(@RequestBody MemberReqDto memberReqDto) {
        return authService.isActivateByEmail(memberReqDto);
    }

    // 아이디+이메일로 유저 탈퇴 확인
    @PostMapping("/isActivate/byIdAndEmail")
    public String isActivateByIdAndEmail(@RequestBody MemberReqDto memberReqDto) {
        return authService.isActivateByIdAndEmail(memberReqDto);
    }

    // 액세스 토큰 재발급
    @PostMapping("/token/refresh")
    public ResponseEntity<TokenDto> refreshToken(@RequestBody String refreshToken, HttpServletRequest request) {
        try {
            if (tokenService.isExcludedPath(request.getRequestURI())) {
                return ResponseEntity.status(400).body(TokenDto.builder()
                        .grantType("Error")
                        .accessToken("")
                        .refreshToken("")
                        .accessTokenExpiresIn(0L)
                        .refreshTokenExpiresIn(0L)
                        .build());
            }

            TokenDto newTokenDto = tokenService.refreshAccessToken(refreshToken, request.getRequestURI());
            return ResponseEntity.ok(newTokenDto);
        } catch (Exception e) {
            TokenDto errorResponse = TokenDto.builder()
                    .grantType("Error")
                    .accessToken("")
                    .refreshToken("")
                    .accessTokenExpiresIn(0L)
                    .refreshTokenExpiresIn(0L)
                    .build();
            return ResponseEntity.status(400).body(errorResponse);
        }
    }
}
