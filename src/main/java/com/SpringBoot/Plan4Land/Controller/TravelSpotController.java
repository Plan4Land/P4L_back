package com.SpringBoot.Plan4Land.Controller;

import com.SpringBoot.Plan4Land.DTO.TravelSpotResDto;
import com.SpringBoot.Plan4Land.Service.TravelSpotService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class TravelSpotController {

    private final TravelSpotService travelSpotService;

    public TravelSpotController(TravelSpotService travelSpotService) {
        this.travelSpotService = travelSpotService;
    }

    // 페이지네이션 처리된 TravelSpot 데이터를 반환
    @GetMapping("/api/travelspots")
    public List<TravelSpotResDto> getTravelSpots(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return travelSpotService.getAllTravelSpots(page, size); // 페이지네이션 처리된 서비스 메서드 호출
    }
}
