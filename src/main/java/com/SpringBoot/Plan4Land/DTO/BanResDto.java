package com.SpringBoot.Plan4Land.DTO;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BanResDto {
    public String userId;
    public String endDate;
    public String reason;
}
