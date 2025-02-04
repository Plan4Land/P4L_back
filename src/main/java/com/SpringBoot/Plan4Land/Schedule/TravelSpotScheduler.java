package com.SpringBoot.Plan4Land.Schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class TravelSpotScheduler {
    @Scheduled(cron = "0 0 2 * * *", zone = "Asia/Seoul") // 초 분 시 일 월 주
    public void runHolidayScript() {
        try {
            Process process = new ProcessBuilder("/usr/bin/python3", "/home/ubuntu/spring_release/travel_spot.py").start();
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                log.info("travel_spot.py 실행 완료");
            } else {
                log.error("travel_spot.py 실행 실패 (Exit Code: " + exitCode + ")");
            }
        } catch (IOException | InterruptedException e) {
            log.error("travel_spot.py 실행 중 오류 발생", e);
            Thread.currentThread().interrupt();
        }
    }
}
