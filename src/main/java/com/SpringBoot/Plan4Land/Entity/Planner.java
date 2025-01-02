package com.SpringBoot.Plan4Land.Entity;

import com.SpringBoot.Plan4Land.Constant.Theme;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

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
    private Theme theme;

    // 플래너 생성일
    private LocalDateTime regDate;
    @PrePersist
    protected void onCreate() {
        this.regDate = LocalDateTime.now();
    }

    // 플래너 소유자
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member owner;

    // 조회수
    private int view;

    // 메모
    private String thumbnail;

    // 공개 여부
    private boolean isPublic;

    // 활성화 여부
    private boolean activate;

}
