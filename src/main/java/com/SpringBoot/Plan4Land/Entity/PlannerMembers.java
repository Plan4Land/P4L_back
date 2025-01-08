package com.SpringBoot.Plan4Land.Entity;

import com.SpringBoot.Plan4Land.Constant.State;
import lombok.*;

import javax.persistence.*;

@Table(name="planner_member")
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PlannerMembers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pl_member_id")
    private Long id;

    // 플래너에 초대된 유저
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    // 플래너
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "planner_id")
    private Planner planner;

    @Enumerated(EnumType.STRING)
    private State state;

    @PrePersist
    protected void onCreate() {
        this.state = State.WAIT;
    }
}
