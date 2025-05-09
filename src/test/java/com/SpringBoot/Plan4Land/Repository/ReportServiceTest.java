package com.SpringBoot.Plan4Land.Repository;

import com.SpringBoot.Plan4Land.DTO.ReportReqDto;
import com.SpringBoot.Plan4Land.Service.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@Slf4j
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class ReportServiceTest {
    @Autowired
    private ReportService reportService;

    @Test
    @DisplayName("신고하기 테스트")
    public void testReport() {
        ReportReqDto reqDto = new ReportReqDto("신고내용", "testid1", "testid2");

        boolean tf = reportService.insertReport(reqDto);

        log.warn("{}", tf);
    }
}
