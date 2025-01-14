package com.SpringBoot.Plan4Land.DTO;

import com.SpringBoot.Plan4Land.Entity.Member;
import com.SpringBoot.Plan4Land.Entity.Planner;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlannerResDto {
    private Long id;
    private String title;
    private String theme;
    private String ownerNickname;
    private String ownerProfileImg;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String area;
    private String subArea;
    private int view;
    private String thumbnail;
    private boolean isPublic;
    private boolean activate;
    private List<PlannerMembersResDto> participants;
    private Long bookmarkCount;

    public static PlannerResDto fromEntity(Planner planner, List<PlannerMembersResDto> participants, Long bookmarkCount) {
        return PlannerResDto.builder()
                .id(planner.getId())
                .title(planner.getTitle())
                .theme(planner.getTheme())
                .ownerNickname(planner.getOwner().getNickname())
                .ownerProfileImg(planner.getOwner().getProfileImg())
                .startDate(planner.getStartDate())
                .endDate(planner.getEndDate())
                .area(planner.getArea())
                .subArea(planner.getSubArea())
                .view(planner.getView())
                .thumbnail(planner.getThumbnail())
                .isPublic(planner.isPublic())
                .activate(planner.isActivate())
                .participants(participants)
                .bookmarkCount(bookmarkCount)
                .build();
    }
}
