package com.SpringBoot.Plan4Land.Service;

import com.SpringBoot.Plan4Land.Constant.Role;
import com.SpringBoot.Plan4Land.DTO.MemberResDto;
import com.SpringBoot.Plan4Land.DTO.TokenDto;
import com.SpringBoot.Plan4Land.Entity.Member;
import com.SpringBoot.Plan4Land.Repository.MemberRepository;
import com.SpringBoot.Plan4Land.Repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {
    private MemberRepository memberRepository;
    private ReportRepository reportRepository;

    public TokenDto adminLogin(String userId, String password) {
        try {
            Member member = memberRepository.findByIdAndPassword(userId, password)
                    .orElseThrow(() -> new RuntimeException("해당 회원이 존재하지 않습니다."));

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(member.getId(), member.getPassword());

            return null;
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public List<MemberResDto> adminGetAllMembers() {
        try {
            List<Member> members = memberRepository.findAll();
            // Dto 새로 생성 or ResDto 수정... 새로 하나 만드는게 나을듯
            //

            return null;
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }

    }

}
