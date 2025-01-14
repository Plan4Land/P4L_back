package com.SpringBoot.Plan4Land.Controller;

import com.SpringBoot.Plan4Land.DTO.TravelSpotResDto;
import com.SpringBoot.Plan4Land.Service.TravelSpotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequiredArgsConstructor
@Slf4j
public class TravelSpotController {

    private final TravelSpotService travelSpotService;


    // 페이지네이션 처리된 TravelSpot 데이터를 반환
    @GetMapping("/api/travelspots")
    public List<TravelSpotResDto> getTravelSpots(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size,
                                                 @RequestParam(required = false) Integer areaCode,
                                                 @RequestParam(required = false) Integer subAreaCode,
                                                 @RequestParam(required = false) String topTheme,
                                                 @RequestParam(required = false) String middleTheme,
                                                 @RequestParam(required = false) String bottomTheme,
                                                 @RequestParam(required = false) String category,
                                                 @RequestParam(required = false) String searchQuery) {
        log.info("{}, {}, {}, {}, \n{}, {}, {}, {} {}",page, size, areaCode, subAreaCode, topTheme, middleTheme, bottomTheme, category, searchQuery);

        List<String> bottomThemeList = (bottomTheme != null && !bottomTheme.isEmpty()) ? List.of(bottomTheme.split(",")) : List.of();
        log.error("bottomThemes : {}", bottomThemeList);

        // 필터링 로직을 추가하여 여행지 데이터를 검색합니다.
        return travelSpotService.getFilteredTravelSpots(page, size, areaCode, subAreaCode,
                topTheme, middleTheme, bottomThemeList, category, searchQuery);
    }

    // 여행지 상세 정보 조회
    @GetMapping("/api/travelspotInfo/{spotId}")
    public TravelSpotResDto getSpotDetail(@PathVariable Long spotId) {
        log.info("여기 찍기 : {}", spotId);
        return travelSpotService.getSpotDetail(spotId); // 서비스 메서드 호출
    }
}
