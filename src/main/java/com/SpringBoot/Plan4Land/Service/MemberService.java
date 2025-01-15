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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    // 회원 검색
    public List<MemberResDto> searchMember(String id, String nickname) {
        List<Member> members = memberRepository.findByIdOrNickname(id, nickname);
        return members.stream()
                .map(this::convertEntityToDto)
                .collect(Collectors.toList());
    }

    // 회원 수정
    public boolean updateMember(MemberReqDto memberReqDto) {
        try {
            Member member = memberRepository.findById(memberReqDto.getId())
                    .orElseThrow(()->new RuntimeException("해당 회원이 존재하지 않습니다."));
            member.setEmail(memberReqDto.getEmail());
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

    // 회원 비밀번호 수정
    public boolean updateMemberPassword(MemberReqDto memberReqDto) {
        try {
            Member member = memberRepository.findById(memberReqDto.getId())
                    .orElseThrow(()->new RuntimeException("해당 회원이 존재하지 않습니다."));
            member.setPassword(memberReqDto.getPassword());

            // 비밀번호 암호화
            String encodedPassword = passwordEncoder.encode(memberReqDto.getPassword());
            member.setPassword(encodedPassword);

            memberRepository.save(member);
            return true;
        } catch (Exception e) {
            log.error("비밀번호 변경: {}", e.getMessage());
            return false;
        }
    }

    // 회원 삭제
    public boolean deleteMember(String userId) {
        try {
            Member member = memberRepository.findById(userId).orElseThrow(()->new RuntimeException("해당 회원이 존재하지 않습니다."));
            member.setSignOutDate(LocalDateTime.now());
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
        Member member = memberRepository.findById(id)
                .orElseThrow(()->new RuntimeException("사용자를 찾을 수 없습니다."));
        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new RuntimeException("비밀번호가 같지 않습니다.");
        }
        return true;
    }

    // 회원 아이디 중복 확인
    public boolean checkIdDuplicate(String id) {
        return memberRepository.existsById(id);
    }

    // 회원 이메일 중복 확인
    public boolean checkEmailDuplicate(String email) {
        return memberRepository.existsByEmail(email);
    }

    // 회원 닉네임 중복 확인
    public boolean checkNicknameDuplicate(String nickname) {
        return memberRepository.existsByNickname(nickname);
    }

    // 회원 아이디 찾기
    public String findMemberId(String name, String email) {
        Member member = memberRepository.findByNameAndEmail(name, email)
                .orElseThrow(()->new RuntimeException("해당 회원이 존재하지 않습니다."));
        return member != null ? member.getId() : null;
    }

    // 회원 비밀번호 찾기
    public boolean findMemberPassword(String id, String email) {
        Member member = memberRepository.findByIdAndEmail(id, email)
                .orElseThrow(()->new RuntimeException("해당 회원이 존재하지 않습니다."));
        return member != null;
    }

    // Member Entity => MemberResDto 변환
    private MemberResDto convertEntityToDto(Member member) {
        MemberResDto memberResDto = new MemberResDto();
        memberResDto.setId(member.getId());
        memberResDto.setEmail(member.getEmail());
        memberResDto.setName(member.getName());
        memberResDto.setNickname(member.getNickname());
        memberResDto.setImgPath(member.getProfileImg());
        memberResDto.setRegDate(member.getSignUpDate());
        return memberResDto;
    }
}
