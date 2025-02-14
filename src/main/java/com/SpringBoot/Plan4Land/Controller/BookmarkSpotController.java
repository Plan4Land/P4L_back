package com.SpringBoot.Plan4Land.Controller;

import com.SpringBoot.Plan4Land.DTO.TravelSpotReqDto;
import com.SpringBoot.Plan4Land.Service.BookMarkSpotService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/bookmarks")
@RequiredArgsConstructor
public class BookmarkSpotController {

    private final BookMarkSpotService bookMarkSpotService;

    // 북마크 추가
    @PostMapping("/add")
    public ResponseEntity<String> addBookmark(@RequestParam String memberId, @RequestParam String spotId) {
        String response = bookMarkSpotService.addBookmark(memberId, spotId);
        return ResponseEntity.ok(response);
    }

    // 북마크 삭제
    @DeleteMapping("/remove")
    public ResponseEntity<String> removeBookmark(@RequestParam String memberId, @RequestParam String spotId) {
        String response = bookMarkSpotService.removeBookmark(memberId, spotId);
        return ResponseEntity.ok(response);
    }

    // 북마크 상태 조회
    @GetMapping("/status")
    public ResponseEntity<Boolean> getBookmarkStatus(@RequestParam String memberId, @RequestParam String spotId) {
        boolean isBookmarked = bookMarkSpotService.isBookmarked(memberId, spotId);
        return ResponseEntity.ok(isBookmarked);
    }

    // 내 북마크 관광지 조회
    @GetMapping("/myBookmarks")
    public ResponseEntity<Page<TravelSpotReqDto>> getBookmarkedSpots(
            @RequestParam String memberId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size) {

        // 페이지네이션 처리된 북마크한 여행지 목록 가져오기
        Page<TravelSpotReqDto> bookmarkedSpots = bookMarkSpotService.getBookmarkedSpots(memberId, page, size);

        return ResponseEntity.ok(bookmarkedSpots);
    }

}
