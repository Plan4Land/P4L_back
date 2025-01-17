package com.SpringBoot.Plan4Land.Service;

import com.SpringBoot.Plan4Land.DTO.HolidayDto;
import com.SpringBoot.Plan4Land.Entity.Holiday;
import com.SpringBoot.Plan4Land.Repository.HolidayRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Month;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HolidayService {
    private final HolidayRepository holidayRepository;

    public List<HolidayDto> getHolidaysByYearAndMonth(Integer year, Integer month) {
        // DB에서 연도와 월에 해당하는 공휴일 목록 조회
        List<Holiday> holidays = holidayRepository.findByYearAndHolidayDateMonth(year, month);

        // DTO로 변환하여 반환
        return holidays.stream()
                .map(holiday -> new HolidayDto(holiday.getHolidayName(), holiday.getHolidayDate()))
                .collect(Collectors.toList());
    }
}

