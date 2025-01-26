package com.SpringBoot.Plan4Land.Service;

import com.SpringBoot.Plan4Land.Constant.Role;
import com.SpringBoot.Plan4Land.Constant.State;
import com.SpringBoot.Plan4Land.DTO.AccessTokenDto;
import com.SpringBoot.Plan4Land.DTO.MemberReqDto;
import com.SpringBoot.Plan4Land.DTO.MemberResDto;
import com.SpringBoot.Plan4Land.DTO.TokenDto;
import com.SpringBoot.Plan4Land.Entity.Ban;
import com.SpringBoot.Plan4Land.Entity.Member;
import com.SpringBoot.Plan4Land.Entity.Report;
import com.SpringBoot.Plan4Land.Entity.Token;
import com.SpringBoot.Plan4Land.JWT.TokenProvider;
import com.SpringBoot.Plan4Land.Repository.BanRepository;
import com.SpringBoot.Plan4Land.Repository.MemberRepository;
import com.SpringBoot.Plan4Land.Repository.ReportRepository;
import com.SpringBoot.Plan4Land.Repository.TokenRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class AdminService {
    private final TokenRepository tokenRepository;
    private final AuthenticationManagerBuilder managerBuilder; // 인증 담당 클래스
    private MemberRepository memberRepository;
    private ReportRepository reportRepository;
    private BanRepository banRepository;
    private TokenProvider tokenProvider;

    @Transactional
    public TokenDto adminLoginWithToken(MemberReqDto memberReqDto) {
        try {
            Member member = memberRepository.findById(memberReqDto.getId())
                    .orElseThrow(() -> new RuntimeException("해당 관리자를 찾을 수 없습니다."));

            // 해당 메소드 설정에 따라 정보를 담은 토큰 생성(여기선 아이디와 비밀번호)
            UsernamePasswordAuthenticationToken authenticationToken = memberReqDto.toAuthentication();
            log.info("UsernamePasswordAuthenticationToken: {}", authenticationToken);

            // 여기서 CustomAuthenticationProvider의 authenticate 실행 (이 안에서 비밀번호도 검증)
            Authentication authentication = managerBuilder.getObject().authenticate(authenticationToken);
            log.info("Authentication 객체: {}", authentication);

            // Authentication 객체의 권한 출력
            log.info("Authentication 권한: {}", authentication.getAuthorities());

            TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);


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
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    // 액세스 토큰 재발급
    public AccessTokenDto accessTokenAgain(String refreshToken) {
        try {
            if (tokenRepository.existsByRefreshToken(refreshToken)) {
                if (tokenProvider.validateToken(refreshToken)) {
                    return tokenProvider.generateAccessTokenDto(tokenProvider.getAuthentication(refreshToken));
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    public List<MemberResDto> adminSearchMember(String keyword, String select) {
        try {
            if (select == null) {
                select = "";
            }
            if (keyword == null) {
                keyword = "";
            }
            List<Member> lst;
            // id, nickname, name, email
            if (!select.isEmpty() && !keyword.isEmpty()) {
                lst = memberRepository.adminFindFilterMember(select, keyword);
            } else if (select.isEmpty() && !keyword.isEmpty()) {
                lst = memberRepository.adminFindMember(keyword, keyword, keyword, keyword);
            } else {
                lst = memberRepository.findAll();
            }


            return lst.stream()
                    .map(this::convertEntityToDto)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }

    }

    public boolean reportProcess(Long reportId, boolean status) {
        try {
            Report report = reportRepository.findById(reportId).
                    orElseThrow(() -> new RuntimeException("해당 신고가 존재하지 않음"));
            if (status) {
                report.setState(State.ACCEPT);
            } else {
                report.setState(State.REJECT);

            }
            reportRepository.save(report);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    @Transactional
    public boolean memberBan(String userId, int day) {
        try {
            Member member = memberRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("해당 유저를 찾을 수 없음"));

            LocalDateTime endDate = LocalDateTime.now().plusDays(day).with(LocalTime.of(0, 0));

            Ban ban = Ban.builder()
                    .member(member)
                    .startDate(LocalDateTime.now())
                    .endDate(endDate)
                    .build();

            member.setRole(Role.ROLE_BANNED);

            banRepository.save(ban);
            memberRepository.save(member);

            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    // Member Entity => MemberResDto 변환
    public MemberResDto convertEntityToDto(Member member) {
        MemberResDto memberResDto = new MemberResDto();
        memberResDto.setUid(member.getUid());
        memberResDto.setId(member.getId());
        memberResDto.setEmail(member.getEmail());
        memberResDto.setName(member.getName());
        memberResDto.setNickname(member.getNickname());
        memberResDto.setImgPath(member.getProfileImg());
        memberResDto.setRegDate(member.getSignUpDate());
        memberResDto.setRole(member.getRole());
        return memberResDto;
    }

}
