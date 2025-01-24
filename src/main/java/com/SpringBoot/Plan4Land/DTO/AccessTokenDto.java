package com.SpringBoot.Plan4Land.DTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccessTokenDto {
    private String grantType;
    private String accessToken;
    private Long accessTokenExpiresIn;
}
