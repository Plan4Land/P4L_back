//package com.SpringBoot.Plan4Land.Repository;
//
//import com.SpringBoot.Plan4Land.DTO.TravelSpotReqDto;
//import com.SpringBoot.Plan4Land.Service.BookMarkSpotService;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.domain.Page;
//import org.springframework.test.context.TestPropertySource;
//
//@Slf4j
//@SpringBootTest
//@TestPropertySource(locations = "classpath:application-test.properties")
//public class BookmarkServiceTest {
//    @Autowired
//    private BookMarkSpotService bookMarkSpotService;
//
//    @Test
//    @DisplayName("관광지 북마크 (중복)추가")
//    public void addSpotBookmark() {
//        String str = bookMarkSpotService.addBookmark("testid1", "125266");
//        log.warn(str);
//    }
//
//
//    @Test
//    @DisplayName("관광지 (미존재)북마크 삭제")
//    public void removeSpotBookmark() {
//        String str = bookMarkSpotService.removeBookmark("testid1", "125266");
//        log.warn(str);
//    }
//
//    @Test
//    @DisplayName("관광지 북마크 여부 확인")
//    public void isBookmarkedTest() {
//        boolean tf = bookMarkSpotService.isBookmarked("testid1", "12527");
//
//        log.warn("tf: {}", tf);
//    }
//
//    @Test
//    @DisplayName("북마크 목록 확인")
//    public void bookmarkSpotListTest(){
//        Page<TravelSpotReqDto> bk = bookMarkSpotService.getBookmarkedSpots("testid1", 0, 3);
//
//        log.warn(bk.getContent().toString(), bk.getTotalElements(), bk.getTotalPages());
//    }
//}
