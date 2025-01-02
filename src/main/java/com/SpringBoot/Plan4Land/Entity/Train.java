package com.SpringBoot.Plan4Land.Entity;

import lombok.*;

import javax.persistence.*;

@Table(name="train")
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Train {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "train_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tp_id")
    private Transport transport;
}