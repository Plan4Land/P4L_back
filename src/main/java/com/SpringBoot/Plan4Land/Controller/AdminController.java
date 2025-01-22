package com.SpringBoot.Plan4Land.Controller;

import com.SpringBoot.Plan4Land.DTO.MemberReqDto;
import com.SpringBoot.Plan4Land.DTO.MemberResDto;
import com.SpringBoot.Plan4Land.DTO.ReportResDto;
import com.SpringBoot.Plan4Land.Service.AdminService;
import com.SpringBoot.Plan4Land.Service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.List;

@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;
    private final ReportService reportService;

    @PostMapping("/admin-login")
    public ResponseEntity<Boolean> adminLogin(@RequestBody MemberReqDto memberReqDto) {
        boolean isAdmin = adminService.adminLogin(memberReqDto.getId(), memberReqDto.getPassword());

        return ResponseEntity.ok(isAdmin);
    }

    @GetMapping("/member-search")
    public ResponseEntity<List<MemberResDto>> memberSearch(@RequestParam(required = false) String select,
                                                           @RequestParam(required = false) String keyword){
        List<MemberResDto> lst = adminService.adminSearchMember(keyword, select);

        log.info(lst.toString());

        return ResponseEntity.ok(lst);
    }

    @GetMapping("/report-list")
    public ResponseEntity<List<ReportResDto>> reportList(){
        List<ReportResDto> lst = reportService.getReports();

        return ResponseEntity.ok(lst);
    }

    @GetMapping("/report-count")
    public ResponseEntity<Integer> reportCount(@RequestParam String userId){
        Integer i = reportService.reportCount(userId);

        return ResponseEntity.ok(i);
    }

    @PostMapping("/report-manage")
    @Transactional
    public ResponseEntity<Boolean> reportManage(@RequestParam Long reportId,
                                                @RequestParam boolean isAccept,
                                                @RequestParam(required = false) String userId,
                                                @RequestParam(required = false) Integer day){
        boolean isSuccess = adminService.reportProcess(reportId, isAccept);
        if(userId != null){
            adminService.memberBan(userId, day);
        }

        return ResponseEntity.ok(isSuccess);
    }

    @PostMapping("/member-ban")
    public ResponseEntity<Boolean> banManage(@RequestParam String userId,
                                             @RequestParam int day){
        boolean isSuccess = adminService.memberBan(userId, day);

        return ResponseEntity.ok(isSuccess);
    }
}
