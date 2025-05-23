package com.SpringBoot.Plan4Land.Entity;

import com.SpringBoot.Plan4Land.Constant.Theme;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Table(name="planner")
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Planner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "planner_id")
    private Long id;

    // 플래너 제목
    private String title;

    // 플래너 테마
    private String theme;

    // 플래너 생성일
    private LocalDateTime regDate;

    // 플래너 소유자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @JsonIgnore
    private Member owner;

    // 여행 시작일
    private LocalDateTime startDate;
    
    // 여행 종료일
    private LocalDateTime endDate;

    // 여행 지역 1차 분류
    private String area;

    // 여행 지역 2차 분류
    private String subArea;

    // 조회수
    private int view;

    // 썸네일
    private String thumbnail;

    // 공개 여부
    private boolean isPublic;

    // 활성화 여부
    private boolean activate;

    @PrePersist
    protected void onCreate() {
        this.regDate = LocalDateTime.now();
        this.activate = true;
        this.view = 0;
    }

    @Builder
    public Planner(String title, String theme, Member owner, LocalDateTime startDate, LocalDateTime endDate, String area, String subArea, String thumbnail, boolean isPublic){
        this.title = title;
        this.theme = theme;
        this.owner = owner;
        this.startDate = startDate;
        this.endDate = endDate;
        this.area = area;
        this.subArea = subArea;
        this.thumbnail = thumbnail;
        this.isPublic = isPublic;
    }

    @OneToMany(mappedBy = "planner", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Plan> plans = new ArrayList<>();

    @OneToMany(mappedBy = "planner", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ChatMsg> chatMsgs = new ArrayList<>();

    @OneToMany(mappedBy = "planner", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<PlannerMembers> plannerMembers = new ArrayList<>();

    @OneToMany(mappedBy = "planner", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<BookmarkPlanner> bookmarkPlanners = new ArrayList<>();
}
