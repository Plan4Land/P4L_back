package com.SpringBoot.Plan4Land.DTO;

import java.time.LocalDate;

public class HolidayDto {

    private String holidayName;
    private LocalDate holidayDate;

    public HolidayDto(String holidayName, LocalDate holidayDate) {
        this.holidayName = holidayName;
        this.holidayDate = holidayDate;
    }

    public String getHolidayName() {
        return holidayName;
    }

    public void setHolidayName(String holidayName) {
        this.holidayName = holidayName;
    }

    public LocalDate getHolidayDate() {
        return holidayDate;
    }

    public void setHolidayDate(LocalDate holidayDate) {
        this.holidayDate = holidayDate;
    }
}
