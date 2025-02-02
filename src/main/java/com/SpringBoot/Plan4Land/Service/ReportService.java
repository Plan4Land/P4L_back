package com.SpringBoot.Plan4Land.Service;

import com.SpringBoot.Plan4Land.Constant.State;
import com.SpringBoot.Plan4Land.DTO.ReportReqDto;
import com.SpringBoot.Plan4Land.DTO.ReportResDto;
import com.SpringBoot.Plan4Land.Entity.Member;
import com.SpringBoot.Plan4Land.Entity.Report;
import com.SpringBoot.Plan4Land.Repository.MemberRepository;
import com.SpringBoot.Plan4Land.Repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    public Page<ReportResDto> getReports(int currentPage, int size, String keyword, String select) {

        try {
            if (select == null) {
                select = "";
            }
            if (keyword == null) {
                keyword = "";
            }
            log.info("키워드 : {}", keyword);
            log.info("셀렉트 : {}", select);
            State state;
            Page<Report> page;
            Pageable pageable = PageRequest.of(currentPage, size);

            switch (select) {
                case "REJECT" -> state = State.REJECT;
                case "ACCEPT" -> state = State.ACCEPT;
                default -> state = State.WAIT;
            }

            if (!select.isEmpty() && !keyword.isEmpty()) {
                page = reportRepository.findReportSelectAndKeyword(pageable, keyword, state);
            } else if (select.isEmpty() && !keyword.isEmpty()) {
                page = reportRepository.findReportByKeyword(pageable, keyword);
            } else if (!select.isEmpty()) {
                page = reportRepository.findReportByState(pageable, state);
            } else {
                page = reportRepository.findAllOrderByStateAndReportDate(pageable);
            }

            return page.map(ReportResDto::of);
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public int reportCount(String userId) {
        Member user = memberRepository.findById(userId).orElseThrow(() -> new RuntimeException("해당 유저를 찾을 수 없습니다."));

        return reportRepository.countReportByReportedAndStateIsNot(user, State.REJECT);
    }


}
