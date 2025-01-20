package com.SpringBoot.Plan4Land.Controller;

import com.SpringBoot.Plan4Land.DTO.MemberReqDto;
import com.SpringBoot.Plan4Land.DTO.MemberResDto;
import com.SpringBoot.Plan4Land.DTO.ReportReqDto;
import com.SpringBoot.Plan4Land.Service.MemberService;
import com.SpringBoot.Plan4Land.Service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.util.*;

@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final ReportService reportService;

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

    // 회원 상세 조회 - 소셜ID로
    @GetMapping("/social/{sso}/{socialId}")
    public ResponseEntity<MemberResDto> memberDetailBySocialId(@PathVariable String sso, @PathVariable Long socialId) {
        MemberResDto memberResDto = memberService.getMemberDetailBySocialId(sso, socialId);
        return ResponseEntity.ok(memberResDto);
    }

    // 회원 검색
    @GetMapping("/search")
    public ResponseEntity<List<MemberResDto>> searchMember(@RequestParam String id, @RequestParam String nickname, @RequestParam Long plannerId) {
        List<MemberResDto> list = memberService.searchMember(id, nickname, plannerId);
        return ResponseEntity.ok(list);
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
        String password = memberService.generateTempPassword();
        return isSuccess ? password : null;
    }

    // 팔로잉
    @PostMapping("/follow")
    public ResponseEntity<Boolean> followMember(@RequestParam String followerId,
                                                @RequestParam String followedId,
                                                @RequestParam boolean isFollow) {
        if(followedId.equals(followerId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(isFollow);
        }
        boolean isSuccess = memberService.followManagement(followerId, followedId, isFollow);

        log.info("followerId: {}, followedId: {} , {}", followerId, followedId, isFollow);
        return ResponseEntity.ok(isSuccess);
    }

    // 팔로잉 정보
    @GetMapping("/follow-info/{userId}")
    public ResponseEntity<Map<String, List<MemberResDto>>> loadFollowMember(@PathVariable String userId) {
        List<MemberResDto> followingInfo = memberService.loadFollowInfo(userId, true);
        List<MemberResDto> followerInfo = memberService.loadFollowInfo(userId, false);
        Map<String, List<MemberResDto>> followInfo = new HashMap<>();
        followInfo.put("followingInfo", followingInfo);
        followInfo.put("followerInfo", followerInfo);

        log.info("followInfo: {}", followInfo);

        return ResponseEntity.ok(followInfo);
    }

    // 신고하기
    @PostMapping("/report")
    public ResponseEntity<Boolean> reportMember(@RequestBody ReportReqDto reportReqDto) {

        log.info("reportMember: {} \n reported : {} , \n content : {}", reportReqDto.getReporter(), reportReqDto.getReported(), reportReqDto.getContent());
        boolean isSuccess = reportService.insertReport(reportReqDto);

        return ResponseEntity.ok(isSuccess);
    }
}
