package com.SpringBoot.Plan4Land.Entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Table(name="Ban")
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Ban {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ban_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_uid")
    private Member member;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private boolean isEnd;

    @Builder
    public Ban(Member member, LocalDateTime startDate, LocalDateTime endDate) {
        this.member = member;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isEnd = false;
    }
}
