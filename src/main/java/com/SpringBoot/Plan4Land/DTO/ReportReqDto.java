package com.SpringBoot.Plan4Land.DTO;

import com.SpringBoot.Plan4Land.Entity.Member;
import com.SpringBoot.Plan4Land.Entity.Report;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportReqDto {
    private String content;
    private String reporter;
    private String reported;


    public Report toEntity(String content, Member reporter, Member reported) {
        return Report.builder()
                .content(content)
                .reporter(reporter)
                .reported(reported)
                .build();
    }

}
