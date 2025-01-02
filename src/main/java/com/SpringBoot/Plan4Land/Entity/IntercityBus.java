package com.SpringBoot.Plan4Land.Entity;

import lombok.*;

import javax.persistence.*;

@Table(name="intercity_bus")
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class IntercityBus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "icbus_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tp_id")
    private Transport transport;
}
