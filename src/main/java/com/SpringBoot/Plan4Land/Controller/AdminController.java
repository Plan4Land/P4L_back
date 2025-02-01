package com.SpringBoot.Plan4Land.Controller;

import com.SpringBoot.Plan4Land.DTO.*;
import com.SpringBoot.Plan4Land.Service.AdminService;
import com.SpringBoot.Plan4Land.Service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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


    // 관리자 로그인
    @PostMapping("/admin-login")
    public ResponseEntity<TokenDto> adminLogin(@RequestBody MemberReqDto memberReqDto) {
        try {
            log.info("Admin Login Request: {}", memberReqDto);
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

    // 관리자 토큰 리프레시
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


    // 관리자 멤버 검색
    @GetMapping("/member-search")
    public ResponseEntity<Page<MemberResDto>> memberSearch(@RequestParam(defaultValue = "0") int currentPage,
                                                           @RequestParam(defaultValue = "20") int pageSize,
                                                           @RequestParam(required = false) String select,
                                                           @RequestParam(required = false) String keyword) {
        log.info("MemberSearch Request: {}", keyword);
        Page<MemberResDto> page = adminService.adminSearchMember(currentPage, pageSize, keyword, select);

        return ResponseEntity.ok(page);
    }


    // 관리자 신고 목록 검색
    @GetMapping("/report-list")
    public ResponseEntity<Page<ReportResDto>> reportList(@RequestParam(defaultValue = "0") int currentPage,
                                                         @RequestParam(defaultValue = "20") int pageSize,
                                                         @RequestParam(required = false) String select,
                                                         @RequestParam(required = false) String keyword) {
        Page<ReportResDto> lst = reportService.getReports(currentPage, pageSize, keyword, select);

        return ResponseEntity.ok(lst);
    }

    // 신고당한 횟수
    @GetMapping("/report-count")
    public ResponseEntity<Integer> reportCount(@RequestParam String userId) {
        Integer i = reportService.reportCount(userId);

        return ResponseEntity.ok(i);
    }

    // 신고 관리
    @PostMapping("/report-manage")
    @Transactional
    public ResponseEntity<Boolean> reportManage(@RequestParam Long reportId,
                                                @RequestParam boolean status,
                                                @RequestParam(required = false) String userId,
                                                @RequestParam(required = false) Integer day,
                                                @RequestParam(required = false) String reason) {
        try {
            boolean isSuccess = adminService.reportProcess(reportId, status);
            if (userId != null) {
                adminService.memberBan(userId, day, reason);
            }
            return ResponseEntity.ok(isSuccess);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }

    }

    // 유저 정지
    @PostMapping("/member-ban")
    public ResponseEntity<Boolean> banManage(@RequestParam String userId,
                                             @RequestParam int day,
                                             @RequestParam String reason) {
        boolean isSuccess = adminService.memberBan(userId, day, reason);

        return ResponseEntity.ok(isSuccess);
    }
}
