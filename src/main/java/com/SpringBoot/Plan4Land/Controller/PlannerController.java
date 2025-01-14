package com.SpringBoot.Plan4Land.Controller;

import com.SpringBoot.Plan4Land.DTO.PlannerReqDto;
import com.SpringBoot.Plan4Land.DTO.PlannerResDto;
import com.SpringBoot.Plan4Land.Entity.Planner;
import com.SpringBoot.Plan4Land.Repository.PlannerRepository;
import com.SpringBoot.Plan4Land.Service.PlannerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    @GetMapping("/planners")
    public Page<PlannerResDto> getAllPlanners(
            @RequestParam(defaultValue = "0") int page,  // 현재 페이지
            @RequestParam(defaultValue = "10") int size // 페이지 크기
    ) {
        // Pageable 객체 생성
        Pageable pageable = PageRequest.of(page, size);

        // 서비스 호출
        return plannerService.findAllPlanners(pageable);
    }

}
