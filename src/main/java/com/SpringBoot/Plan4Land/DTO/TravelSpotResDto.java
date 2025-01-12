package com.SpringBoot.Plan4Land.DTO;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TravelSpotResDto {
    private Long id;
    private String title;
    private String tel;
    private String thumbnail;
    private int areaCode;
    private int sigunguCode;
    private String addr1;
    private String addr2;
    private String cat1;
    private String cat2;
    private String cat3;
    private String typeId;
    private LocalDateTime createdTime;
    private LocalDateTime modifiedTime;
    private double mapX;
    private double mapY;
    private int bookmark;
}
