package com.SpringBoot.Plan4Land.Service;

import com.SpringBoot.Plan4Land.DTO.ReportReqDto;
import com.SpringBoot.Plan4Land.Entity.Member;
import com.SpringBoot.Plan4Land.Entity.Report;
import com.SpringBoot.Plan4Land.Repository.MemberRepository;
import com.SpringBoot.Plan4Land.Repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {
    private final ReportRepository reportRepository;
    private final MemberRepository memberRepository;

    public boolean insertReport(ReportReqDto reportReqDto) {
        try {
            Member reporter = memberRepository.findById(reportReqDto.getReporter())
                    .orElseThrow(() -> new RuntimeException("해당 회원을 찾을 수 없습니다"));

            Member reported = memberRepository.findById(reportReqDto.getReported())
                    .orElseThrow(() -> new RuntimeException("해당 회원을 찾을 수 없습니다"));

            Report report = reportReqDto.toEntity(reportReqDto.getContent(), reporter, reported);

            reportRepository.save(report);

            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }
}
