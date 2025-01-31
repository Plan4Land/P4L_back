package com.SpringBoot.Plan4Land.Service;

import com.SpringBoot.Plan4Land.DTO.AccessTokenDto;
import com.SpringBoot.Plan4Land.DTO.TokenDto;
import com.SpringBoot.Plan4Land.Entity.Token;
import com.SpringBoot.Plan4Land.JWT.JwtFilter;
import com.SpringBoot.Plan4Land.JWT.TokenProvider;
import com.SpringBoot.Plan4Land.Repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {
    private final TokenProvider tokenProvider;
    private final TokenRepository tokenRepository;

    // 리프레시 토큰을 사용하여 액세스 토큰 재발급
    public AccessTokenDto refreshAccessToken(String refreshToken) {
        // 리프레시 토큰 값에서 큰따옴표 제거 (프론트에서 전달 시 JSON 형태라 큰따옴표가 앞뒤로 붙어서 나옴)
        String trimmedToken = refreshToken.replace("\"", "").trim();
        Optional<Token> tokenOptional = tokenRepository.findByRefreshToken(trimmedToken);
        if (!tokenOptional.isPresent()) {
            throw new RuntimeException("리프레시 토큰이 존재하지 않습니다.");
        }

        // 리프레시 토큰 유효성 검증
        if (!tokenProvider.validateToken(trimmedToken)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "리프레시 토큰이 유효하지 않습니다.");
        }

        // 새 액세스 토큰 생성
        try {
            Authentication authentication = tokenProvider.getAuthentication(trimmedToken);
            log.info("액세스 토큰 재발급");
            return tokenProvider.generateAccessTokenDto(authentication);
        } catch (RuntimeException e) {
            log.error("토큰 생성 실패: {}", e.getMessage(), e);
            throw new RuntimeException("토큰 생성에 실패했습니다.", e);
        }
    }


    // 인증이 필요 없는 경로인지 확인
    public boolean isExcludedPath(String requestUri) {
        return requestUri.startsWith("/static/**") ||
                requestUri.startsWith("/auth/**") ||
                requestUri.startsWith("/ws/**") ||
                requestUri.startsWith("/api/travelspots") ||
                requestUri.startsWith("/member/idExists/**") ||
                requestUri.startsWith("/member/emailExists/**") ||
                requestUri.startsWith("/member/nicknameExists/**") ||
                requestUri.startsWith("/member/find-id") ||
                requestUri.startsWith("/member/find-password") ||
                requestUri.startsWith("/planner/fetchData/**") ||
                requestUri.startsWith("/bookmarkPlanner/plannersTop3");
    }
}
