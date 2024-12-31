package com.SpringBoot.Plan4Land.Entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Table(name="payment")
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    private LocalDate paymentDate;
}
