package com.SpringBoot.Plan4Land.Entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Table(name="membership")
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Membership {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "membership_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String payType;

    private LocalDateTime expiryDate;

    private LocalDateTime paymentDate;

    private String billingKey;

    private boolean activated;
}
