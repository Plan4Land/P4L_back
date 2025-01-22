package com.SpringBoot.Plan4Land.Service;

import com.SpringBoot.Plan4Land.DTO.TokenDto;
import com.SpringBoot.Plan4Land.Entity.Token;
import com.SpringBoot.Plan4Land.JWT.TokenProvider;
import com.SpringBoot.Plan4Land.Repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final TokenProvider tokenProvider;
    private final TokenRepository tokenRepository;

    // 리프레시 토큰을 사용하여 액세스 토큰 재발급
    public TokenDto refreshAccessToken(String refreshToken) {
        // 리프레시 토큰 검증
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다.");
        }

        // 리프레시 토큰 확인
        Token token = tokenRepository.findByRefreshToken(refreshToken).orElseThrow(() -> new IllegalArgumentException("해당하는 리프레시 토큰이 없습니다."));

        // 리프레시 토큰으로 새로운 액세스 토큰 생성
        Authentication authentication = tokenProvider.getAuthentication(refreshToken);
        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);

        return tokenDto;
    }
}
