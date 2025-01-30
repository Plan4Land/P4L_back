package com.SpringBoot.Plan4Land.Service;

import com.SpringBoot.Plan4Land.Constant.Role;
import com.SpringBoot.Plan4Land.DTO.MemberResDto;
import com.SpringBoot.Plan4Land.DTO.MemberReqDto;
import com.SpringBoot.Plan4Land.DTO.TokenDto;
import com.SpringBoot.Plan4Land.Entity.Member;
import com.SpringBoot.Plan4Land.Entity.Token;
import com.SpringBoot.Plan4Land.JWT.TokenProvider;
import com.SpringBoot.Plan4Land.Repository.MemberRepository;
import com.SpringBoot.Plan4Land.Repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Slf4j // 로그 정보를 출력하기 위함
@Service // 스프링 컨테이너에 빈(bean, 객체) 등록
@RequiredArgsConstructor // 생성자를 자동으로 생성
@Transactional // 여러개의 작업을 하나의 논리적인 단위로 묶어줌
public class AuthService {
    // 생성자를 통한 의존성 주입, 생성자를 통해서 의존성 주입을 받는 경우 Autowired 생략
    private final MemberRepository memberRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder managerBuilder; // 인증을 담당하는 클래스
    private final AuthenticationManager authenticationManager;

    // 회원 가입 여부
    public boolean isMember(String userId) {
        return memberRepository.existsById(userId);
    }

    // 회원 가입
    public MemberResDto signUp(MemberReqDto memberReqDto) {
        if (memberRepository.existsById(memberReqDto.getId())) {
            throw new RuntimeException("이미 가입되어 있는 유저입니다.");
        } else {
            Member member = memberReqDto.toEntity(passwordEncoder);
            return MemberResDto.of(memberRepository.save(member));
        }
    }

    // 로그인
    public TokenDto login(MemberReqDto memberReqDto) {
        try {
            Member member;
            // 소셜 로그인
            if (memberReqDto.getSso() != null) {
                member = memberRepository.findBySsoAndSocialId(memberReqDto.getSso(), memberReqDto.getSocialId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "회원가입이 필요합니다."));

                // 회원 상태 검증
                if (!member.isActivate()) {
                    log.error("탈퇴한 회원입니다.");
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "탈퇴한 회원입니다.");
                }
            } else {
                // 일반 로그인
                member = memberRepository.findById(memberReqDto.getId())
                        .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

                // 회원 상태 검증
                if (!member.isActivate()) {
                    log.error("탈퇴한 회원입니다.");
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "탈퇴한 회원입니다.");
                }
            }

            // 권한을 포함하여 UsernamePasswordAuthenticationToken 생성
            UsernamePasswordAuthenticationToken authenticationToken = memberReqDto.toAuthentication();

            Authentication authentication = managerBuilder.getObject().authenticate(authenticationToken);

            // 토큰 생성
            TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);

            // 리프레시 토큰 만료 시간 추출
            if (tokenRepository.existsByMember(member)) {
                tokenRepository.deleteByMember(member);
            }

            Token token = new Token();
            String encodedToken = tokenDto.getRefreshToken();
            token.setRefreshToken(encodedToken);
            token.setExpiration(tokenDto.getRefreshTokenExpiresIn());
            token.setIssuedAt(LocalDateTime.now());
            token.setMember(member);

            tokenRepository.save(token);

            return tokenDto;
        } catch (BadCredentialsException e) {
            log.error("Bad credentials provided: ", e);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다.");
        } catch (ResponseStatusException e) {
            log.error("Response status exception: ", e);
            throw e;
        } catch (Exception e) {
            log.error("Authentication failed: ", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "로그인 중 문제가 발생했습니다.");
        }
    }

    // 로그아웃
    public void logout(MemberReqDto memberReqDto) {
        log.warn(memberReqDto.toString());
        // ID로 사용자 검색
        Member member = memberRepository.findById(memberReqDto.getId())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 토큰 삭제
        List<Token> tokens = tokenRepository.findAllByMember(member);
        tokenRepository.deleteAll(tokens);

        log.info("로그아웃 성공: 리프레시 토큰 삭제.");
    }

    // 이메일로 유저 탈퇴 확인
    public String isActivateByEmail(MemberReqDto memberReqDto) {
        // ID로 사용자 검색
        Member member = memberRepository.findByEmail(memberReqDto.getEmail())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 회원 상태 검증
        if (!member.isActivate()) {
            return "탈퇴한 회원입니다.";
        } else {
            return "활성 회원입니다.";
        }
    }

    // 아이디+이메일로 유저 탈퇴 확인
    public String isActivateByIdAndEmail(MemberReqDto memberReqDto) {
        // ID로 사용자 검색
        Member member = memberRepository.findByIdAndEmail(memberReqDto.getId(), memberReqDto.getEmail())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 회원 상태 검증
        if (!member.isActivate()) {
            return "탈퇴한 회원입니다.";
        } else {
            return "활성 회원입니다.";
        }
    }
}
