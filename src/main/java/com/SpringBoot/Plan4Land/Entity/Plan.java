package com.SpringBoot.Plan4Land.Entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Table(name="plan")
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Plan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plan_id")
    private Long id;

    // 계획 순서(같은 날짜에서)
    private int seq;

    // 계획일
    private LocalDateTime date;

    // 장소명
    private String spotName;

    // 카테고리
    private String category;

    // 간단메모
    private String memo;

    // 위도
    private String latitude;

    // 경도
    private String longitude;

    @ManyToOne
    @JoinColumn(name = "planner_id")
    private Planner planner;
}
