package com.SpringBoot.Plan4Land.DTO;

import com.SpringBoot.Plan4Land.Entity.Member;
import com.SpringBoot.Plan4Land.Entity.Planner;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @ToString
@NoArgsConstructor @AllArgsConstructor
@Builder
public class PlannerReqDto {
    private String title;
    private String theme;
    private String id;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String area;
    private String subArea;
    private String thumbnail;

    @JsonProperty("isPublic")
    private boolean isPublic;

    public Planner toEntity(Member owner){
        return Planner.builder()
                .title(title)
                .theme(theme)
                .owner(owner)
                .startDate(this.startDate)
                .endDate(this.endDate)
                .area(this.area)
                .subArea(this.subArea)
                .thumbnail(this.thumbnail)
                .isPublic(this.isPublic)
                .build();

    }
}
