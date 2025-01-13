package com.SpringBoot.Plan4Land.Controller;

import com.SpringBoot.Plan4Land.Service.BookmarkPlannerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/bookmarkPlanner")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class BookmarkPlannerController {
    private final BookmarkPlannerService bookmarkPlannerService;

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
}
