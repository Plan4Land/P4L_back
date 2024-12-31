package com.SpringBoot.Plan4Land.Entity;

import lombok.*;

import javax.persistence.*;

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
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member follower;

    // 팔로우 당한 사람
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member followed;
}
