package com.SpringBoot.Plan4Land.Controller;

import com.SpringBoot.Plan4Land.DTO.MemberResDto;
import com.SpringBoot.Plan4Land.DTO.MemberReqDto;
import com.SpringBoot.Plan4Land.DTO.TokenDto;
import com.SpringBoot.Plan4Land.Service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    // 카카오 로그인 처리
//    @PostMapping("/kakao")
//    public ResponseEntity<TokenDto> kakaoLogin(@RequestBody String kakaoToken) {
//        // 카카오 API를 통해 kakaoToken으로 사용자 정보를 받아옵니다.
//        MemberReqDto memberReqDto = authService.getKakaoUser(kakaoToken);
//
//        if (memberReqDto == null) {
//            return ResponseEntity.status(400).body(null); // 카카오 사용자 정보 조회 실패 시 처리
//        }
//
//        // 카카오 ID로 사용자 존재 여부 확인
//        boolean isUserExist = authService.isMember(memberReqDto.getKakaoId());
//
//        if (isUserExist) {
//            // 기존 사용자 로그인 처리
//            TokenDto token = authService.login(memberReqDto);
//            return ResponseEntity.ok(token);
//        } else {
//            // 새로운 사용자일 경우 회원가입 처리
//            MemberResDto newUser = authService.signUp(memberReqDto);
//            TokenDto token = authService.login(memberReqDto); // 회원가입 후 로그인 처리
//            return ResponseEntity.ok(token);
//        }
//    }
}
