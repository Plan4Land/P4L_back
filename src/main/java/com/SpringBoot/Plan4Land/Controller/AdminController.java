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
                                                           @RequestParam String keyword){
        List<MemberResDto> lst = adminService.adminSearchMember(keyword, select);

        return ResponseEntity.ok(lst);
    }

    @GetMapping("/report-list")
    public ResponseEntity<List<ReportResDto>> reportList(){
        List<ReportResDto> lst = reportService.getReports();

        return ResponseEntity.ok(lst);
    }
}
