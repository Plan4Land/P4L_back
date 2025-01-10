package com.SpringBoot.Plan4Land.Controller;

import com.SpringBoot.Plan4Land.Service.BookMarkSpotService;
import com.SpringBoot.Plan4Land.Service.BookMarkSpotService.SpotDetailsResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookmarks")
@CrossOrigin(origins = "http://localhost:3000")

public class BookmarkSpotController {

    private final BookMarkSpotService bookMarkSpotService;

    public BookmarkSpotController(BookMarkSpotService bookMarkSpotService) {
        this.bookMarkSpotService = bookMarkSpotService;
    }

    @GetMapping("/{spotId}")
    public ResponseEntity<SpotDetailsResponse> getSpotDetailsWithBookmark(@PathVariable String spotId) {
        SpotDetailsResponse response = bookMarkSpotService.getSpotDetailsWithBookmark(spotId);
        return ResponseEntity.ok(response);
    }
}
