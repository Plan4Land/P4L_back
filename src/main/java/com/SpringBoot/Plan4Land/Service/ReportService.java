package com.SpringBoot.Plan4Land.Service;

import com.SpringBoot.Plan4Land.DTO.ReportReqDto;
import com.SpringBoot.Plan4Land.DTO.ReportResDto;
import com.SpringBoot.Plan4Land.Entity.Member;
import com.SpringBoot.Plan4Land.Entity.Report;
import com.SpringBoot.Plan4Land.Repository.MemberRepository;
import com.SpringBoot.Plan4Land.Repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

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

    public List<ReportResDto> getReports(int currentPage, int pageSize, String keyword, String select) {
        List<Report> lst = reportRepository.findAllOrderByStateAndReportDate();

        return lst.stream().map(ReportResDto::of).collect(Collectors.toList());
    }

    public int reportCount(String userId) {
        Member user = memberRepository.findById(userId).orElseThrow(() -> new RuntimeException("해당 유저를 찾을 수 없습니다."));
        int cnt = reportRepository.countReportByReported(user);

        return cnt;
    }


}
