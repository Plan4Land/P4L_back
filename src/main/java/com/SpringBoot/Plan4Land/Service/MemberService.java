package com.SpringBoot.Plan4Land.Service;

import com.SpringBoot.Plan4Land.DTO.MemberReqDto;
import com.SpringBoot.Plan4Land.DTO.MemberResDto;
import com.SpringBoot.Plan4Land.DTO.PlannerResDto;
import com.SpringBoot.Plan4Land.Entity.Follow;
import com.SpringBoot.Plan4Land.Entity.Member;
import com.SpringBoot.Plan4Land.Entity.Planner;
import com.SpringBoot.Plan4Land.Entity.PlannerMembers;
import com.SpringBoot.Plan4Land.Repository.FollowRepository;
import com.SpringBoot.Plan4Land.Repository.MemberRepository;
import com.SpringBoot.Plan4Land.Repository.PlannerMembersRepository;
import com.SpringBoot.Plan4Land.Repository.PlannerRepository;
import com.sun.tools.jconsole.JConsoleContext;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PlannerMembersRepository plannerMembersRepository;
    private final FollowRepository followRepository;

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
        Member member = memberRepository.findById(userId).orElseThrow(() -> new RuntimeException("해당 회원이 존재하지 않습니다."));
        log.info("Detail");
        return convertEntityToDto(member);
    }

    // 회원 상세 조회 - 소셜 ID로
    public MemberResDto getMemberDetailBySocialId(String sso, String socialId) {
        Member member = memberRepository.findBySsoAndSocialId(sso, socialId).orElseThrow(() -> new RuntimeException("해당 회원이 존재하지 않습니다."));
        return convertEntityToDto(member);
    }

    // 회원 검색
    public List<MemberResDto> searchMember(String id, String nickname, Long plannerId) {
        List<Member> members = memberRepository.findByIdOrNickname(id, nickname);

        return members.stream()
                .map(member -> {
                    // 회원에 해당하는 plannerId의 상태 조회
                    Optional<PlannerMembers> plannerMember = plannerMembersRepository
                            .findByMemberIdAndPlannerId(member.getId(), plannerId);

                    // 상태가 존재하면 상태를 추가, 없으면 null
                    String state = plannerMember.map(plannerMembers -> plannerMembers.getState().name()).orElse(null);

                    // 변환된 DTO 객체 생성
                    return MemberResDto.of(member, state); // 상태를 함께 설정
                })
                .collect(Collectors.toList());
    }

    // 회원 수정
    public boolean updateMember(MemberReqDto memberReqDto) {
        try {
            Member member = memberRepository.findById(memberReqDto.getId())
                    .orElseThrow(() -> new RuntimeException("해당 회원이 존재하지 않습니다."));
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
                    .orElseThrow(() -> new RuntimeException("해당 회원이 존재하지 않습니다."));
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
            Member member = memberRepository.findById(userId).orElseThrow(() -> new RuntimeException("해당 회원이 존재하지 않습니다."));
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
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
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
                .orElseThrow(() -> new RuntimeException("해당 회원이 존재하지 않습니다."));
        return member != null ? member.getId() : null;
    }

    // 회원 비밀번호 찾기
    public boolean findMemberPassword(String id, String email) {
        Member member = memberRepository.findByIdAndEmail(id, email)
                .orElseThrow(() -> new RuntimeException("해당 회원이 존재하지 않습니다."));
        return member != null;
    }

    // 임시 비밀번호 생성 함수
    public String generateTempPassword() {
        SecureRandom random = new SecureRandom();
        // 각 조건을 만족하는 문자 그룹
        String upperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCase = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String specialChars = "!@#$%^&*()";
        // 결과 비밀번호를 구성할 리스트
        List<Character> password = new ArrayList<>();
        // 각 그룹에서 최소 1개의 문자를 추가
        password.add(upperCase.charAt(random.nextInt(upperCase.length())));
        password.add(lowerCase.charAt(random.nextInt(lowerCase.length())));
        password.add(digits.charAt(random.nextInt(digits.length())));
        password.add(specialChars.charAt(random.nextInt(specialChars.length())));
        // 나머지 문자 채우기
        String allChars = upperCase + lowerCase + digits + specialChars;
        for (int i = 4; i < 8; i++) { // 총 8자리로 생성
            password.add(allChars.charAt(random.nextInt(allChars.length())));
        }
        // 비밀번호를 랜덤하게 섞기
        Collections.shuffle(password, random);
        // 리스트를 문자열로 변환
        StringBuilder result = new StringBuilder();
        for (char c : password) {
            result.append(c);
        }
        return result.toString();
    }

    // 팔로우/해제
    public boolean followManagement(String follower, String followed, boolean isFollow) {
        try {
            if (isFollow) {
                Member followerMember = memberRepository.findByIdAndActivate(follower, true)
                        .orElseThrow(() -> new RuntimeException("해당 회원을 찾을 수 없습니다."));
                Member followedMember = memberRepository.findByIdAndActivate(followed, true)
                        .orElseThrow(() -> new RuntimeException("해당 회원을 찾을 수 없습니다."));

                Follow follow = new Follow(followerMember, followedMember);

                if(followRepository.existsByFollowedAndFollower(followedMember, followerMember)){
                    log.warn("이미 팔로우가 존재합니다.");
                    return false;
                }

                followRepository.save(follow);
            } else {
                Follow follow = followRepository.findByFollowerIdAndFollowedId(follower, followed);

                followRepository.delete(follow);
            }
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    // 팔로우 정보 반환
    public List<MemberResDto> loadFollowInfo(String userId, boolean following) {
        try {
            log.info("유저 아이디 : {}", userId);
            Member member = memberRepository.findById(userId).orElseThrow(() -> new RuntimeException("회원을 찾지 못했습니다"));
            log.info("획득한 유저의 아이디 : {}", member.getId());
            log.info("획득한 유저의 uid : {}", member.getUid());
            List<Long> lst;
            if (following) {
                // 해당 유저가 팔로우 한 사람
                lst = followRepository.getFollowedIdBy(member.getUid());
            } else {
                // 해당 유저의 팔로워
                lst = followRepository.getFollowerIdBy(member.getUid());
            }

            log.warn(lst.toString());
            List<Member> members = memberRepository.findAllById(lst);

            return members.stream()
                    .map(this::convertEntityToDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }


    // Member Entity => MemberResDto 변환
    private MemberResDto convertEntityToDto(Member member) {
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
