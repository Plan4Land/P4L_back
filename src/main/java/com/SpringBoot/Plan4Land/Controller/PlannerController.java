package com.SpringBoot.Plan4Land.Controller;

import com.SpringBoot.Plan4Land.DTO.PlannerReqDto;
import com.SpringBoot.Plan4Land.DTO.PlannerResDto;
import com.SpringBoot.Plan4Land.Entity.Planner;
import com.SpringBoot.Plan4Land.Repository.PlannerRepository;
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
    @PostMapping("/insert")
    public ResponseEntity<Long> makePlanner(@RequestBody PlannerReqDto plannerReqDto) {
        log.error(plannerReqDto.toString());
        Long isSuccess = plannerService.makePlanner(plannerReqDto);
        log.info(isSuccess.toString());
        return ResponseEntity.ok(isSuccess);
    }

    // 플래너 조회
    @GetMapping("/{plannerId}")
    public ResponseEntity<PlannerResDto> getPlanner(@PathVariable Long plannerId) {
        PlannerResDto plannerResDto = plannerService.findByPlannerId(plannerId);
        return ResponseEntity.ok(plannerResDto);
    }
}
