package com.SpringBoot.Plan4Land.Repository;

import com.SpringBoot.Plan4Land.DTO.MemberReqDto;
import com.SpringBoot.Plan4Land.DTO.TokenDto;
import com.SpringBoot.Plan4Land.Entity.Member;
import com.SpringBoot.Plan4Land.Service.AuthService;
import com.SpringBoot.Plan4Land.Service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;

@Slf4j
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class AuthServiceTest {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private AuthService authService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("회원 가입 테스트")
    public void signUp() {
        for (int i = 1; i <= 10; i++) {
            Member member = Member.builder().
                    email("email" + i + "@email.com").
                    id("testid" + i).
                    password(passwordEncoder.encode("asdf!1234")).
                    name("실험이름" + i).
                    nickname("testnick" + i).build();

            memberRepository.save(member);
        }
    }

    @Test
    @DisplayName("회원가입 여부")
    public void isMemberTest(){
        boolean isMember1 = authService.isMember("testid1");
        boolean isMember2 = authService.isMember("notmember");

        log.info("isMember1: {}",isMember1);
        log.info("isMember2: {}",isMember2);
    }

    @Test
    @DisplayName("아이디 중복 확인")
    public void idDuplicationTest(){
        boolean isDup = memberService.checkIdDuplicate("testid1");
        boolean isDup2 = memberService.checkIdDuplicate("notid11");

        log.info("isDup1: {}, isDup2 : {}",isDup, isDup2);
    }

    @Test
    @DisplayName("이메일 중복 확인")
    public void emailDuplicationTest(){
        boolean isDup = memberService.checkEmailDuplicate("email1@email.com");
        boolean isDup2 = memberService.checkEmailDuplicate("not@email.com");

        log.info("isDup1: {}, isDup2 : {}",isDup, isDup2);
    }

    @Test
    @DisplayName("닉네임 중복 확인")
    public void nicknameDuplicationTest(){
        boolean isDup = memberService.checkNicknameDuplicate("testnick1");
        boolean isDup2 = memberService.checkNicknameDuplicate("notnick");

        log.info("isDup1: {}, isDup2 : {}",isDup, isDup2);
    }

    @Test
    @DisplayName("로그인 테스트 - 성공")
    public void loginSuccessTest() {
        MemberReqDto memberReqDto = new MemberReqDto("testid1", null, "asdf!1234", null, null, null, null, null);

        TokenDto tokenDto = authService.login(memberReqDto);

        Assertions.assertNotNull(tokenDto);
        Assertions.assertNotNull(tokenDto.getAccessToken());
        Assertions.assertNotNull(tokenDto.getRefreshToken());
    }

    @Test
    @DisplayName("로그인 테스트 - 실패")
    public void loginFailTest() {
        MemberReqDto memberReqDto = new MemberReqDto("testid1", null, "1234", null, null, null, null, null);

        Exception exception = Assertions.assertThrows(RuntimeException.class,
                () -> { authService.login(memberReqDto); });

        Assertions.assertEquals("Authentication failed due to bad credentials", exception.getMessage());
    }

    @Test
    @DisplayName("로그아웃 테스트")
    public void logoutTest() {
        MemberReqDto memberReqDto = new MemberReqDto();
        memberReqDto.setId("testid1");

        authService.logout(memberReqDto);
    }

    @Test
    @DisplayName("이메일로 회원 확인")
    public void isMemberByEmailTest() {
        MemberReqDto memberReqDto1 = new MemberReqDto();
        MemberReqDto memberReqDto2 = new MemberReqDto();
        memberReqDto1.setEmail("email1@email.com");
        memberReqDto2.setEmail("email10@email.com");

        String str1 = authService.isActivateByEmail(memberReqDto1);
        log.warn(str1);
        String str2 = authService.isActivateByEmail(memberReqDto2);
        log.warn(str2);
    }

    @Test
    @DisplayName("이메일과 아이디로 회원 확인")
    public void isMemberByEmailAndIdTest() {
        MemberReqDto memberReqDto1 = new MemberReqDto();
        MemberReqDto memberReqDto2 = new MemberReqDto();
        memberReqDto1.setEmail("email1@email.com");
        memberReqDto1.setId("testid1");
        memberReqDto2.setEmail("email10@email.com");
        memberReqDto1.setId("testid10");

        String str1 = authService.isActivateByEmail(memberReqDto1);
        log.warn(str1);
        String str2 = authService.isActivateByEmail(memberReqDto2);
        log.warn(str2);
    }

    @Test
    @DisplayName("아이디 찾기")
    public void findIdTest() {
        String id = memberService.findMemberId("실험이름1", "email1@email.com");
        log.warn("{} ", id);
        String notId = memberService.findMemberId("실험이름11", "email11@email.com");

        log.warn("{} {}", id, notId);

    }

}
