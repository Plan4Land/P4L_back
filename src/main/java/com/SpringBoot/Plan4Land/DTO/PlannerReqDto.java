package com.SpringBoot.Plan4Land.DTO;

import lombok.*;

@Getter
@Setter
@ToString
public class PlannerReqDto {
    private String title;
    private String theme;
    private String id;
    private String thumbnail;
    private boolean isPublic;
}
