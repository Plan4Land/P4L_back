package com.SpringBoot.Plan4Land.Service;

import com.SpringBoot.Plan4Land.DTO.TokenDto;
import com.SpringBoot.Plan4Land.Entity.Token;
import com.SpringBoot.Plan4Land.JWT.JwtFilter;
import com.SpringBoot.Plan4Land.JWT.TokenProvider;
import com.SpringBoot.Plan4Land.Repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {
    private final TokenProvider tokenProvider;
    private final TokenRepository tokenRepository;

    // 리프레시 토큰을 사용하여 액세스 토큰 재발급
    public TokenDto refreshAccessToken(String refreshToken) {
        log.info("서비스 refreshToken: {}", refreshToken);
        log.info("DB에 저장된 토큰: {}", tokenRepository.findByRefreshToken(refreshToken));
        log.info("토큰있는지: {}", tokenRepository.existsByRefreshToken(refreshToken));
//        // 토큰 있는지 확인
//        if (!tokenRepository.existsByRefreshToken(refreshToken)) {
//            throw new RuntimeException("리프레시 토큰이 존재하지 않습니다.");
//        }

        // 토큰 검증
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new RuntimeException("리프레시 토큰이 유효하지 않습니다.");
        }

        // 새 토큰 생성
        try {
            return tokenProvider.generateTokenDto(tokenProvider.getAuthentication(refreshToken));
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
