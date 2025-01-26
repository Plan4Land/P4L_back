package com.SpringBoot.Plan4Land.JWT;

import com.SpringBoot.Plan4Land.Constant.Role;
import com.SpringBoot.Plan4Land.DTO.AccessTokenDto;
import com.SpringBoot.Plan4Land.DTO.TokenDto;
import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class TokenProvider {
    private static final String AUTHORITIES_KEY = "auth";
    private static final String ROLE = "role";
    private final Key key;
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 60; // 1시간  * 60 * 24 * 7
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 7L; // 7일

    public TokenProvider(Dotenv dotenv) {
        String secretKey = dotenv.get("JWT_SECRET");
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    // 새 토큰 생성
    public TokenDto generateTokenDto(Authentication authentication) {
        // 사용자 역할(role) 클레임을 가져오기
        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> authority.startsWith("ROLE_"))
                .findFirst().orElseThrow(()-> new RuntimeException("사용자 역할을 찾을 수 없습니다"));

        log.info("사용자 역할 클레임 : {}", role);

        long now = (new Date()).getTime();
        Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME); // 1분
        Date refreshTokenExpiresIn = new Date(now + REFRESH_TOKEN_EXPIRE_TIME); // 24일

        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim(ROLE, role)
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        String refreshToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim(ROLE, role)
                .setExpiration(refreshTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        return TokenDto.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .accessTokenExpiresIn(accessTokenExpiresIn.getTime())
                .refreshToken(refreshToken)
                .refreshTokenExpiresIn(refreshTokenExpiresIn.getTime())
                .build();
    }

    // 액세스 토큰 생성
    public AccessTokenDto generateAccessTokenDto(Authentication authentication) {
        // 사용자 역할(role) 클레임을 가져오기
        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> Arrays.stream(Role.values()).map(Role::name).anyMatch(roleIs -> roleIs.equals(authority)))
                .findFirst().orElse("");

        long now = (new Date()).getTime();
        Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME); // 1분


        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim(ROLE, role)
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        return AccessTokenDto.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .accessTokenExpiresIn(accessTokenExpiresIn.getTime())
                .build();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);
        log.info("JWT Claims: {}", claims);

        if (claims.get(ROLE) == null) {
            throw new RuntimeException("권한 정보가 없습니다.");
        }

        List<GrantedAuthority> authorities = Arrays.stream(new String[]{claims.get(ROLE).toString()})
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        User principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public Authentication getAdminAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);

        if (claims.get(AUTHORITIES_KEY) == null || !claims.get(ROLE).equals("ROLE_ADMIN")) {
            throw new RuntimeException("권한 정보가 없습니다.");
        }

        List<String> rolesAndAuthorities = new ArrayList<>(Arrays.asList(claims.get(AUTHORITIES_KEY).toString().split(",")));
        rolesAndAuthorities.add(claims.get(ROLE).toString());

        Collection<? extends GrantedAuthority> authorities =
                rolesAndAuthorities.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        User principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, accessToken, authorities);
    }


    public boolean validateToken(String token) {
        try {
            // 토큰을 파싱하고 검증
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
            return false;  // 만료된 토큰의 경우 false 반환
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        } catch (JwtException e) {
            log.error("JWT 파싱 오류: {}", e.getMessage());
        }
        return false;
    }


    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        } catch (JwtException e) {
            log.error("JWT 파싱 오류: {}", e.getMessage());
            throw new RuntimeException("JWT 파싱 실패");
        }
    }


}
