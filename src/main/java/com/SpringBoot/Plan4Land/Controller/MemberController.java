package com.SpringBoot.Plan4Land.Controller;

import com.SpringBoot.Plan4Land.DTO.MemberReqDto;
import com.SpringBoot.Plan4Land.DTO.MemberResDto;
import com.SpringBoot.Plan4Land.Service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
