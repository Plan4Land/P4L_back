package com.SpringBoot.Plan4Land.Controller;

import com.SpringBoot.Plan4Land.DTO.PlannerReqDto;
import com.SpringBoot.Plan4Land.DTO.PlannerResDto;
import com.SpringBoot.Plan4Land.Entity.BookmarkPlanner;
import com.SpringBoot.Plan4Land.Entity.Member;
import com.SpringBoot.Plan4Land.Entity.Planner;
import com.SpringBoot.Plan4Land.Repository.PlannerRepository;
import com.SpringBoot.Plan4Land.Service.BookmarkPlannerService;
import com.SpringBoot.Plan4Land.Service.PlannerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/planner")
@RequiredArgsConstructor
public class PlannerController {
    private final PlannerService plannerService;
    private final BookmarkPlannerService bookmarkPlannerService;


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

    // 플래너 목록 조회
    @GetMapping("/planners")
    public ResponseEntity<Page<PlannerResDto>> getAllPlanners(@RequestParam(defaultValue = "0") int currentPage,  // 현재 페이지
                                                              @RequestParam(defaultValue = "10") int pageSize,
                                                              @RequestParam(required = false) String areaCode,
                                                              @RequestParam(required = false) String subAreaCode,
                                                              @RequestParam(required = false) String themeList,
                                                              @RequestParam(required = false) String searchQuery,
                                                              @RequestParam(defaultValue = "LatestDesc") String sortBy) {

        // 서비스 호출
        Page<PlannerResDto> dto = plannerService.getFilterdPlanner(currentPage, pageSize,areaCode, subAreaCode, themeList, searchQuery,
                 sortBy);

        log.info(dto.getContent().toString());
        log.info("dto.getTotalElements() : " + dto.getTotalElements());
        log.info("dto.getTotalPages() : " + dto.getTotalPages());

        return ResponseEntity.ok(dto);
    }

    // 특정 유저가 작성한 플래너 리스트
    @GetMapping("/myPlanners")
    public Page<PlannerResDto> getPlannersByOwner(
            @RequestParam String memberId,
            @RequestParam int page,
            @RequestParam int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<Planner> planners = plannerService.getPlannersByOwner(memberId, pageable);
        return planners.map(planner -> PlannerResDto.fromEntity(planner, null, null));
    }

    // 플래너에 멤버 초대
    @PostMapping("/invite")
    public ResponseEntity<Boolean> inviteMember(@RequestParam String memberId, @RequestParam Long plannerId) {
        boolean isSuccess = plannerService.inviteMember(memberId, plannerId);
        return ResponseEntity.ok(isSuccess);
    }

    // 초대된 플래너 조회
    @GetMapping("/invite/{memberId}")
    public ResponseEntity<List<PlannerResDto>> selectInvitedPlanners(@PathVariable String memberId) {
        List<PlannerResDto> invitedPlanners = plannerService.selectInvitedPlanners(memberId);
        return ResponseEntity.ok(invitedPlanners);
    }

    // 초대된 플래너 승인
    @PostMapping("/invite/accept")
    public ResponseEntity<Boolean> acceptInvitation(@RequestParam String memberId, @RequestParam Long plannerId) {
        boolean isSuccess = plannerService.acceptInvitation(memberId, plannerId);
        return ResponseEntity.ok(isSuccess);
    }

    // 초대된 플래너 거절
    @DeleteMapping("/invite/reject")
    public ResponseEntity<Boolean> rejectInvitation(@RequestParam String memberId, @RequestParam Long plannerId) {
        boolean isSuccess = plannerService.rejectInvitation(memberId, plannerId);
        return ResponseEntity.ok(isSuccess);
    }
}
