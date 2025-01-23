package com.SpringBoot.Plan4Land.Controller;

import com.SpringBoot.Plan4Land.DTO.PlannerResDto;
import com.SpringBoot.Plan4Land.Entity.BookmarkPlanner;
import com.SpringBoot.Plan4Land.Service.BookmarkPlannerService;
import com.SpringBoot.Plan4Land.Service.PlannerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/bookmarkPlanner")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class BookmarkPlannerController {
    private final BookmarkPlannerService bookmarkPlannerService;
    private final PlannerService plannerService;

    // 북마크 상태 조회
    @GetMapping()
    public ResponseEntity<Boolean> isBookmarked(@RequestParam String memberId, @RequestParam Long plannerId) {
        boolean isBookmarked = bookmarkPlannerService.isBookmarked(memberId, plannerId);
        return ResponseEntity.ok(isBookmarked);
    }

    // 북마크 추가
    @PutMapping()
    public ResponseEntity<Boolean> putBookmarked(@RequestParam String memberId, @RequestParam Long plannerId) {
        boolean isSuccess = bookmarkPlannerService.putBookmarked(memberId, plannerId);
        return ResponseEntity.ok(isSuccess);
    }

    // 북마크 삭제
    @DeleteMapping()
    public ResponseEntity<Boolean> deleteBookmarked(@RequestParam String memberId, @RequestParam Long plannerId) {
        log.info("받은 데이터 : memberId = {}, plannerId = {}", memberId, plannerId);
        boolean isSuccess = bookmarkPlannerService.deleteBookmarked(memberId, plannerId);
        return ResponseEntity.ok(isSuccess);
    }

    // 내가 북마크한 플래너 리스트
    @GetMapping("/myBookmarkPlanners")
    public ResponseEntity<Page<BookmarkPlanner>> getBookmarkedPlanners(
            @RequestParam("memberId") String memberId,  // memberId를 받음
            @RequestParam("page") int page,             // 페이지 번호
            @RequestParam("size") int size) {           // 페이지 크기

        // 북마크된 플래너 목록을 페이지네이션 처리하여 가져오기
        Page<BookmarkPlanner> bookmarkedPlanners = bookmarkPlannerService.getBookmarkedPlanners(memberId, page, size);

        // 페이지네이션된 결과 반환
        return ResponseEntity.ok(bookmarkedPlanners);
    }

    // 북마크 갯수 상위 3개 플래너
    @GetMapping("/plannersTop3")
    public ResponseEntity<List<PlannerResDto>> getTop3BookmarkedPlanners() {
        List<PlannerResDto> topPlanners = plannerService.getTop3BookmarkedPlanners();

        if (topPlanners.size() > 4) {
            topPlanners = topPlanners.subList(0, 4);
        }
        return ResponseEntity.ok(topPlanners);
    }
}
