package com.SpringBoot.Plan4Land.DTO;

import com.SpringBoot.Plan4Land.Constant.State;
import com.SpringBoot.Plan4Land.Entity.Member;
import com.SpringBoot.Plan4Land.Entity.Report;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportResDto {
    private Member reporter;
    private LocalDateTime reportDate;
    private Member reported;
    private String content;
    private State state;

    public static ReportResDto of(Report report) {
        return ReportResDto.builder()
                .reporter(report.getReporter())
                .reportDate(report.getReportDate())
                .reported(report.getReported())
                .content(report.getContent())
                .state(report.getState())
                .build();
    }
}
