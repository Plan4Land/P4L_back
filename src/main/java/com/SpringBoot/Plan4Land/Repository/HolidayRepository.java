package com.SpringBoot.Plan4Land.Repository;

import com.SpringBoot.Plan4Land.Entity.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HolidayRepository extends JpaRepository<Holiday, Long> {
    @Query("SELECT h FROM Holiday h WHERE h.year = :year AND MONTH(h.holidayDate) = :month")
    List<Holiday> findByYearAndHolidayDateMonth(Integer year, Integer month);}
