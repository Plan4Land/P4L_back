package com.SpringBoot.Plan4Land.Controller;

import com.SpringBoot.Plan4Land.DTO.MemberResDto;
import com.SpringBoot.Plan4Land.DTO.MemberReqDto;
import com.SpringBoot.Plan4Land.DTO.TokenDto;
import com.SpringBoot.Plan4Land.Service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    // 유저 존재 확인
    @GetMapping("/exists/{userId}")
    public ResponseEntity<Boolean> isUser(@PathVariable String userId) {
        boolean isTrue = authService.isMember(userId);
        return ResponseEntity.ok(!isTrue);
    }

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<MemberResDto> signup(@RequestBody MemberReqDto memberReqDto) {
        return ResponseEntity.ok(authService.signUp(memberReqDto));
    }
    
    // 로그인
    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@RequestBody MemberReqDto memberReqDto) {
        return ResponseEntity.ok(authService.login(memberReqDto));
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<TokenDto> logout(@RequestBody MemberReqDto memberReqDto) {
        authService.logout(memberReqDto);
        return ResponseEntity.noContent().build();
    }

    // 이메일로 유저 탈퇴 확인
    @PostMapping("/isActivate/byEmail")
    public String isActivateById(@RequestBody MemberReqDto memberReqDto) {
        return authService.isActivateByEmail(memberReqDto);
    }

    // 아이디+이메일로 유저 탈퇴 확인
    @PostMapping("/isActivate/byIdAndEmail")
    public String isActivateByIdAndEmail(@RequestBody MemberReqDto memberReqDto) {
        return authService.isActivateByIdAndEmail(memberReqDto);
    }
}
