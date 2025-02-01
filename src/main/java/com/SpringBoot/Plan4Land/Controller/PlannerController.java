package com.SpringBoot.Plan4Land.Controller;

import com.SpringBoot.Plan4Land.DTO.PlannerReqDto;
import com.SpringBoot.Plan4Land.DTO.PlannerResDto;
import com.SpringBoot.Plan4Land.Entity.BookmarkPlanner;
import com.SpringBoot.Plan4Land.Entity.Member;
import com.SpringBoot.Plan4Land.Entity.Plan;
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
        Long isSuccess = plannerService.makePlanner(plannerReqDto);
        return ResponseEntity.ok(isSuccess);
    }

    // 플래너 수정
    @PostMapping("/update")
    public ResponseEntity<PlannerResDto > editPlannerInfo(@RequestParam Long plannerId, @RequestBody PlannerReqDto plannerReqDto) {
        PlannerResDto isSuccess = plannerService.editPlannerInfo(plannerReqDto, plannerId);
        return ResponseEntity.ok(isSuccess);
    }

    // 플래너 공개여부 수정
    @PostMapping("/update/isPublic")
    public ResponseEntity<PlannerResDto> updateIsPublic(@RequestParam Long plannerId, @RequestParam boolean isPublic) {
        PlannerResDto updatedPlanner = plannerService.updateIsPublic(plannerId, isPublic);
        return ResponseEntity.ok(updatedPlanner);
    }

    // 플래너  상세조회
    @GetMapping("/fetchData/{plannerId}")
    public ResponseEntity<PlannerResDto> getPlanner(@PathVariable Long plannerId) {
        PlannerResDto plannerResDto = plannerService.findByPlannerId(plannerId);
        return ResponseEntity.ok(plannerResDto);
    }

    // 플래너 삭제
    @DeleteMapping("/delete-planner")
    public ResponseEntity<Boolean> removePlanner(@RequestParam Long plannerId, @RequestParam String userId) {
        boolean isSuccess = plannerService.removePlannerInfo(plannerId, userId);
        return ResponseEntity.ok(isSuccess);
    }

    // 플래너 멤버 탈퇴
    @DeleteMapping("/delete-planner-member")
    public ResponseEntity<Boolean> leavePlanner(@RequestParam Long plannerId, @RequestParam String userId) {
        boolean isSuccess = plannerService.leavePlanner(plannerId, userId);
        return ResponseEntity.ok(isSuccess);
    }

    // Plan 조회
    @GetMapping("/fetchData/getPlan")
    public ResponseEntity<List<Plan>> getPlans(@RequestParam Long plannerId) {
        List<Plan> plans = plannerService.getPlans(plannerId);
        return ResponseEntity.ok(plans);
    }



    // Plan 삭제 및 삽입
    @PostMapping("/updatePlan")
    public ResponseEntity<List<Plan>> deleteAndInsertPlans(@RequestParam Long plannerId, @RequestBody List<Plan> newPlans) {
        log.warn(newPlans.toString());
        List<Plan> plans = plannerService.deleteAndInsertPlans(plannerId, newPlans);
        return ResponseEntity.ok(plans);
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
        Page<PlannerResDto> dto = plannerService.getFilteredPlanner(currentPage, pageSize, areaCode, subAreaCode, themeList, searchQuery,
                 sortBy);

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
// 특정 유저가 작성한 플래너 리스트 중 공개 정보만
    @GetMapping("/userPlanners")
    public Page<PlannerResDto> getPrivatePlannersByOwner(
            @RequestParam String memberId,
            @RequestParam int page,
            @RequestParam int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Planner> planners = plannerService.getPrivatePlannersByOwner(memberId, pageable);
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

    // 내가 포함된, 작성한 플래너 목록
    @GetMapping("/inPlanners")
    public Page<PlannerResDto> getPlanners(
            @RequestParam String memberId,
            @RequestParam int page,
            @RequestParam int size) {

        return plannerService.getPlanners(memberId, page, size);
    }
}
