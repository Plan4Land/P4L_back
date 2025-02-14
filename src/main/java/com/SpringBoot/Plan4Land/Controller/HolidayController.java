package com.SpringBoot.Plan4Land.Controller;

import com.SpringBoot.Plan4Land.DTO.HolidayDto;
import com.SpringBoot.Plan4Land.Service.HolidayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
public class HolidayController {
    private final HolidayService holidayService;

    @GetMapping("/holidays")
    public List<HolidayDto> getHolidaysByYearAndMonth(@RequestParam Integer year, @RequestParam Integer month) {
        return holidayService.getHolidaysByYearAndMonth(year, month);
    }

}
