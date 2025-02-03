//package com.SpringBoot.Plan4Land.Repository;
//
//import com.SpringBoot.Plan4Land.DTO.MemberResDto;
//import com.SpringBoot.Plan4Land.Entity.Follow;
//import com.SpringBoot.Plan4Land.Service.MemberService;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.domain.Page;
//import org.springframework.test.context.TestPropertySource;
//
//import java.util.List;
//
//@Slf4j
//@SpringBootTest
//@TestPropertySource(locations = "classpath:application-test.properties")
//public class MemberServiceTest {
//    @Autowired
//    private MemberService memberService;
//
//    @Test
//    @DisplayName("팔로우/해제 테스트")
//    public void followTest(){
//        boolean tf1 = memberService.followManagement("testid1", "testid3", true);
//        boolean tf2 = memberService.followManagement("testid1", "testid3", true);
//
//        log.info("팔로 1 : {}, 팔로 2 : {}", tf1, tf2);
//    }
//
//    @Test
//    @DisplayName("팔로우 정보 로드")
//    public void followListTest(){
//        List<MemberResDto> follows = memberService.loadFollowInfo("testid1", true);
//        List<MemberResDto> followers = memberService.loadFollowInfo("testid1", false);
//
//        log.warn("팔로잉 : {} \n 팔로워 : {}", follows.toString(), followers.toString());
//    }
//}
