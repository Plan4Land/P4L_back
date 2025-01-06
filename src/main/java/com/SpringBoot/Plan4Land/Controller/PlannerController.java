package com.SpringBoot.Plan4Land.Controller;

import com.SpringBoot.Plan4Land.DTO.PlannerReqDto;
import com.SpringBoot.Plan4Land.Service.PlannerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/planner")
@RequiredArgsConstructor
public class PlannerController {
    private final PlannerService plannerService;

    // 플래너 생성
    @PostMapping("/make")
    public ResponseEntity<Boolean> makePlanner(@RequestBody PlannerReqDto plannerReqDto) {
        log.error(plannerReqDto.toString());
        boolean isSuccess = plannerService.makePlanner(plannerReqDto);
        return ResponseEntity.ok(isSuccess);
    }
}
