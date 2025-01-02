package com.SpringBoot.Plan4Land.Entity;

import lombok.*;

import javax.persistence.*;

@Table(name="express_bus")
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ExpressBus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exbus_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tp_id")
    private Transport transport;
}
