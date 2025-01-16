package com.SpringBoot.Plan4Land.Entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

@Table(name = "Holiday")
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Holiday {

    // 공휴일 ID (Primary Key)
    @Id
    @Column(name = "holiday_id")
    private Long id;

    // 공휴일 이름
    @Column(name = "holiday_name", nullable = false)
    private String holidayName;

    // 공휴일 여부 (Y/N)
    @Column(name = "is_holiday", nullable = false)
    private String isHoliday;

    // 공휴일 날짜 (YYYY-MM-DD)
    @Column(name = "holiday_date", nullable = false)
    private LocalDate holidayDate;

    // 고유 번호 (API의 seq 필드)
    @Column(name = "seq", nullable = true)
    private Integer seq;

    // 해당 연도
    @Column(name = "year", nullable = false)
    private Integer year;
}
