package com.SpringBoot.Plan4Land.Entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Table(name="Chat_Msg")
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ChatMsg {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "msg_id")
    private Long id;

    // 발신자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member sender;

    // 내용
    private String content;

    // 전송시간
    private LocalDateTime sendTime;

    // 플래너
    @ManyToOne
    @JoinColumn(name = "planner_id")
    private Planner planner;

    @PrePersist
    protected void onCreate() {
        this.sendTime = LocalDateTime.now();
    }
}
