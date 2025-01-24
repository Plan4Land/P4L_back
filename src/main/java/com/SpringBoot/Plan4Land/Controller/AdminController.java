package com.SpringBoot.Plan4Land.Controller;

import com.SpringBoot.Plan4Land.DTO.*;
import com.SpringBoot.Plan4Land.Service.AdminService;
import com.SpringBoot.Plan4Land.Service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<TokenDto> adminLogin(@RequestBody MemberReqDto memberReqDto) {
        try {
            TokenDto tokenDto = adminService.adminLoginWithToken(memberReqDto);
            if (tokenDto == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
            return ResponseEntity.ok(tokenDto);
        } catch (Exception e) {
            log.error("관리자 로그인 중 오류 발생: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/token-refresh")
    public ResponseEntity<AccessTokenDto> newAccessToken(@RequestBody String refreshToken) {
        try {
            AccessTokenDto accessTokenDto = adminService.accessTokenAgain(refreshToken);
            if (accessTokenDto == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
            return ResponseEntity.ok(accessTokenDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/member-search")
    public ResponseEntity<List<MemberResDto>> memberSearch(@RequestParam(required = false) String select,
                                                           @RequestParam(required = false) String keyword) {
        List<MemberResDto> lst = adminService.adminSearchMember(keyword, select);

        return ResponseEntity.ok(lst);
    }

    @GetMapping("/report-list")
    public ResponseEntity<List<ReportResDto>> reportList() {
        List<ReportResDto> lst = reportService.getReports();

        return ResponseEntity.ok(lst);
    }

    @GetMapping("/report-count")
    public ResponseEntity<Integer> reportCount(@RequestParam String userId) {
        Integer i = reportService.reportCount(userId);

        return ResponseEntity.ok(i);
    }

    @PostMapping("/report-manage")
    @Transactional
    public ResponseEntity<Boolean> reportManage(@RequestParam Long reportId,
                                                @RequestParam boolean status,
                                                @RequestParam(required = false) String userId,
                                                @RequestParam(required = false) Integer day) {
        try {
            boolean isSuccess = adminService.reportProcess(reportId, status);
            if (userId != null) {
                adminService.memberBan(userId, day);
            }
            return ResponseEntity.ok(isSuccess);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }

    }

    @PostMapping("/member-ban")
    public ResponseEntity<Boolean> banManage(@RequestParam String userId,
                                             @RequestParam int day) {
        boolean isSuccess = adminService.memberBan(userId, day);

        return ResponseEntity.ok(isSuccess);
    }
}
