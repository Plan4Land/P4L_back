package com.SpringBoot.Plan4Land.Service;

import com.SpringBoot.Plan4Land.DTO.MemberReqDto;
import com.SpringBoot.Plan4Land.DTO.MemberResDto;
import com.SpringBoot.Plan4Land.Entity.Member;
import com.SpringBoot.Plan4Land.Repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    // 회원 전체 조회
    public List<MemberResDto> getMemberAllList() {
        List<Member> members = memberRepository.findAll();
        List<MemberResDto> memberResDtoList = new ArrayList<>();
        for (Member member : members) {
            memberResDtoList.add(convertEntityToDto(member));
        }
        return memberResDtoList;
    }

    // 회원 상세 조회
    public MemberResDto getMemberDetail(String userId) {
        Member member = memberRepository.findById(userId).orElseThrow(()->new RuntimeException("해당 회원이 존재하지 않습니다."));
        return convertEntityToDto(member);
    }

    // 회원 수정
    public boolean updateMember(MemberReqDto memberReqDto) {
        try {
            Member member = memberRepository.findById(memberReqDto.getId())
                    .orElseThrow(()->new RuntimeException("해당 회원이 존재하지 않습니다."));
            member.setEmail(memberReqDto.getEmail());
            member.setPassword(memberReqDto.getPassword());
            member.setName(memberReqDto.getName());
            member.setNickname(memberReqDto.getNickname());
            member.setProfileImg(memberReqDto.getProfileImg());
            memberRepository.save(member);
            return true;
        } catch (Exception e) {
            log.error("회원정보 수정 : {}", e.getMessage());
            return false;
        }
    }

    // 회원 삭제
    public boolean deleteMember(String userId) {
        try {
            Member member = memberRepository.findById(userId).orElseThrow(()->new RuntimeException("해당 회원이 존재하지 않습니다."));
            member.setActivate(false);
            memberRepository.save(member);
            return true;
        } catch (Exception e) {
            log.error("회원 삭제에 실패했습니다. : {}", e.getMessage());
            return false;
        }
    }

    // 회원 비밀번호 체크
    public boolean validateMember(String id, String password) {
        Member member = memberRepository.findByIdAndPassword(id, password)
                .orElseThrow(()->new RuntimeException("비밀번호가 같지 않습니다."));
        return passwordEncoder.matches(password, member.getPassword());
    }

    // Member Entity => MemberResDto 변환
    private MemberResDto convertEntityToDto(Member member) {
        MemberResDto memberResDto = new MemberResDto();
        memberResDto.setId(member.getId());
        memberResDto.setEmail(member.getEmail());
        memberResDto.setName(member.getName());
        memberResDto.setNickname(member.getNickname());
        memberResDto.setImgPath(member.getProfileImg());
        memberResDto.setRegDate(member.getSignupDate());
        return memberResDto;
    }
}
