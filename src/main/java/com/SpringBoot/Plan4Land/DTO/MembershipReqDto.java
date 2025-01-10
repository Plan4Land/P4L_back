package com.SpringBoot.Plan4Land.DTO;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MembershipReqDto {
    private String memberId;
    private String billingKey;
    private String payType;
    private LocalDateTime expiryDate;
    private LocalDateTime paymentDate;
    private boolean activated;
}
