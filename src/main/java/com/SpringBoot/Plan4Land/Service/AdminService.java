package com.SpringBoot.Plan4Land.Service;

import com.SpringBoot.Plan4Land.Constant.Role;
import com.SpringBoot.Plan4Land.Constant.State;
import com.SpringBoot.Plan4Land.DTO.MemberResDto;
import com.SpringBoot.Plan4Land.DTO.TokenDto;
import com.SpringBoot.Plan4Land.Entity.Ban;
import com.SpringBoot.Plan4Land.Entity.Member;
import com.SpringBoot.Plan4Land.Entity.Report;
import com.SpringBoot.Plan4Land.JWT.TokenProvider;
import com.SpringBoot.Plan4Land.Repository.BanRepository;
import com.SpringBoot.Plan4Land.Repository.MemberRepository;
import com.SpringBoot.Plan4Land.Repository.ReportRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class AdminService {
    private MemberRepository memberRepository;
    private ReportRepository reportRepository;
    private BanRepository banRepository;
    private MemberService memberService;
    private TokenProvider tokenProvider;
    private AuthenticationManager authenticationManager;

    public TokenDto adminLoginWithToken(String userId, String password) {
        try {
            Member member = memberRepository.findByIdAndPassword(userId, password)
                    .orElseThrow(() -> new RuntimeException("해당 회원이 존재하지 않습니다."));

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(member.getId(), member.getPassword());

            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);

            return null;
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public boolean adminLogin(String userId, String password) {
        try {
            Member member = memberRepository.findByIdAndPassword(userId, password)
                    .orElseThrow(() -> new RuntimeException("해당 회원이 존재하지 않습니다."));

            return member.getRole().equals(Role.ROLE_ADMIN);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<MemberResDto> adminSearchMember(String keyword, String select) {
        try {
            log.warn("{}, {}", keyword, select);
            List<Member> lst;
            // id, nickname, name, email
            if (select != null && keyword != null) {
                lst = memberRepository.adminFindFilterMember(select, keyword);
            } else if (select == null && keyword != null) {
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
                reportRepository.save(report);
                return true;
            } else {
                report.setState(State.REJECT);
                reportRepository.save(report);
                return false;
            }
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

            Ban ban = Ban.builder()
                    .member(member)
                    .startDate(LocalDateTime.now())
                    .endDate(LocalDateTime.now().plusDays(day))
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
