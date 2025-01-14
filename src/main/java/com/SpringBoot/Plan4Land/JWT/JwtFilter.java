package com.SpringBoot.Plan4Land.JWT;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// JWT 추출
// 토큰의 유효성 검증

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 인증이 필요없는 경로를 제외
        if (isExcludedPath(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = resolveToken(request); // 헤더에서 JWT 추출
        if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
            Authentication authentication = tokenProvider.getAuthentication(jwt);
            SecurityContextHolder.getContext().setAuthentication(authentication); // 인증 객체 설정
        }
        filterChain.doFilter(request, response); // 다음 필터로 요청 전달
    }

    // 인증이 필요 없는 경로인지 확인
    private boolean isExcludedPath(String requestUri) {
        return requestUri.startsWith("/static/**") ||
                requestUri.startsWith("/auth/**") ||
                requestUri.startsWith("/ws/**") ||
                requestUri.startsWith("/api/travelspots") ||
                requestUri.startsWith("/member/idExists/**") ||
                requestUri.startsWith("/member/emailExists/**") ||
                requestUri.startsWith("/member/nicknameExists/**") ||
                requestUri.startsWith("/member/find-id") ||
                requestUri.startsWith("/member/find-password");
    }

    // Authorization 헤더에서 토큰 추출
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer " 부분 제거
        }
        return null;
    }
}