package com.SpringBoot.Plan4Land.DTO;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class PlannerReqDto {
    private String title;
    private String theme;
    private String id;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String area;
    private String subArea;
    private String thumbnail;
    private boolean isPublic;
}
