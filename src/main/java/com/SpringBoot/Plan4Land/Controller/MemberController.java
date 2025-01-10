package com.SpringBoot.Plan4Land.Controller;

import com.SpringBoot.Plan4Land.DTO.MemberReqDto;
import com.SpringBoot.Plan4Land.DTO.MemberResDto;
import com.SpringBoot.Plan4Land.Service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    // 전체 회원 조회
    @GetMapping("/list")
    public ResponseEntity<List<MemberResDto>> memberAllList() {
        List<MemberResDto> list = memberService.getMemberAllList();
        return ResponseEntity.ok(list);
    }
    // 회원 상세 조회
    @GetMapping("/{userId}")
    public ResponseEntity<MemberResDto> memberDetail(@PathVariable String userId) {
        MemberResDto memberResDto = memberService.getMemberDetail(userId);
        return ResponseEntity.ok(memberResDto);
    }
    // 회원 정보 수정
    @PutMapping("/update")
    public ResponseEntity<Boolean> memberUpdate(@RequestBody MemberReqDto memberReqDto) {
        boolean isSuccess = memberService.updateMember(memberReqDto);
        return ResponseEntity.ok(isSuccess);
    }
    // 회원 비밀번호 변경
    @PutMapping("/update/password")
    public ResponseEntity<Boolean> memberUpdatePassword(@RequestBody MemberReqDto memberReqDto) {
        boolean isSuccess = memberService.updateMemberPassword(memberReqDto);
        return ResponseEntity.ok(isSuccess);
    }
    // 회원 정보 삭제
    @DeleteMapping("/{userId}")
    public ResponseEntity<Boolean> memberDelete(@PathVariable String userId) {
        boolean isSuccess = memberService.deleteMember(userId);
        return ResponseEntity.ok(isSuccess);
    }
    // 회원 비밀번호 검증
    @PostMapping("/validate")
    public ResponseEntity<Boolean> memberValidate(@RequestBody Map<String, String> memberInfo) {
        // 요청으로 ID와 비밀번호 추출
        String id = memberInfo.get("id");
        String password = memberInfo.get("password");
        // 검증 호출
        boolean isValid = memberService.validateMember(id, password);
        return ResponseEntity.ok(isValid);
    }
    // 회원 아이디 중복 확인
    @PostMapping("/idExists/{id}")
    public boolean memberIdDulicate(@PathVariable String id) {
        return memberService.checkIdDuplicate(id);
    }
    // 회원 이메일 중복 확인
    @PostMapping("/emailExists/{email}")
    public boolean memberEmailDulicate(@PathVariable String email) {
        return memberService.checkEmailDuplicate(email);
    }
    // 회원 닉네임 중복 확인
    @PostMapping("/nicknameExists/{nickname}")
    public boolean memberNicknameDulicate(@PathVariable String nickname) {
        return memberService.checkNicknameDuplicate(nickname);
    }
    // 회원 아이디 찾기
    @PostMapping("/find-id")
    public ResponseEntity<String> findMemberId(@RequestBody MemberReqDto memberReqDto) {
        String userId = memberService.findMemberId(memberReqDto.getName(), memberReqDto.getEmail());

        if (userId != null) {
            return ResponseEntity.ok(userId);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
    }
    // 회원 비밀번호 찾기
    @PostMapping("/find-password")
    public String findMemberPassword(@RequestBody MemberReqDto memberReqDto) {
        boolean isSuccess = memberService.findMemberPassword(memberReqDto.getId(), memberReqDto.getEmail());
        String password = generateTempPassword();
        return isSuccess ? password : null;
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
}
