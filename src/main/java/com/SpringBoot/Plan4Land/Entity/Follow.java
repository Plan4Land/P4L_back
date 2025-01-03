package com.SpringBoot.Plan4Land.Entity;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Table(name="Follow")
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Follow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "follow_id")
    private Long id;

    // 팔로우 한 사람
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id") // 명확성을 위해 이름 변경
    private Member follower;

    // 팔로우 당한 사람
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "followed_id") // 명확성을 위해 이름 변경
    private Member followed;
}
