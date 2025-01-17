package com.SpringBoot.Plan4Land.Controller;

import com.SpringBoot.Plan4Land.DTO.MemberReqDto;
import com.SpringBoot.Plan4Land.Service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @PostMapping("/admin-login")
    public ResponseEntity<Boolean> adminLogin(@RequestBody MemberReqDto memberReqDto) {
        boolean isAdmin = adminService.adminLogin(memberReqDto.getId(), memberReqDto.getPassword());

        return ResponseEntity.ok(isAdmin);
    }
}
