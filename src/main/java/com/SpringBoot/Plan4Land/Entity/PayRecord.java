package com.SpringBoot.Plan4Land.Entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Table(name="pay_record")
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PayRecord {
    @Id
    @Column(name = "record_id")
    private String recordId;

    private String memberId;

    @ManyToOne
    @JoinColumn(name = "membership_id")
    private Membership membership;

    private LocalDate payTime;

    private String payType;
}
