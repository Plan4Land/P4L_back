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
    @JoinColumn(name = "member_uid")
    private Member member;

    private String payType;

    private LocalDateTime expiryDate;

    private LocalDateTime paymentDate;

    private String billingKey;

    @Column(name = "activated", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean activated;

    @Column(name = "cancel", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean cancel;

    @PrePersist
    protected void onCreate() {
        this.activated = true;
        this.cancel = false;
    }
}
