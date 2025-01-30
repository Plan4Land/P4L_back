package com.SpringBoot.Plan4Land.Security;

import com.SpringBoot.Plan4Land.Entity.Member;
import com.SpringBoot.Plan4Land.Repository.MemberRepository;
import com.SpringBoot.Plan4Land.Service.CustomUserDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailService customUserDetailService;

    @Autowired
    public CustomAuthenticationProvider(MemberRepository memberRepository, @Lazy PasswordEncoder passwordEncoder, CustomUserDetailService customUserDetailService) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.customUserDetailService = customUserDetailService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = (String) authentication.getPrincipal();
        String password = (String) authentication.getCredentials();

        // customUserDetailService 통해 유저 정보 획득
        UserDetails userDetails = customUserDetailService.loadUserByUsername(username);

        // 카카오 로그인 처리
        if (password == null || password.isEmpty()) {
            Member member = memberRepository.findById(username)
                    .orElseThrow(() -> new RuntimeException("소셜 사용자를 찾을 수 없습니다."));

            // 카카오 사용자 인증 성공
            return new UsernamePasswordAuthenticationToken(username, password, List.of(new SimpleGrantedAuthority("ROLE_GENERAL")));
        }

        if (userDetails == null || !passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        // 정지 유저 접근 불가
        boolean isBanned = userDetails.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_BANNED"));

        if (isBanned) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "정지된 회원입니다.");
        }

            // 일반 사용자 인증 성공
        return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
