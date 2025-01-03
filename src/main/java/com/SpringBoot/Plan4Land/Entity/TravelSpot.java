package com.SpringBoot.Plan4Land.Entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Table(name="Travel_spot")
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TravelSpot {
    // 콘텐츠(장소) ID
    @Id
    @Column(name = "spot_id")
    private Long id;

    // 장소 이름
    private String title;

    // 전화번호
    private String tel;

    // 시, 도 코드
    private int areaCode;
    // 시군구 코드
    private int sigunguCode;

    // 주소
    private String addr1;

    // 상세주소
    private String addr2;

    // 대분류
    private String cat1;
    // 중분류
    private String cat2;
    // 소분류
    private String cat3;

    // 타입 아이디
    private String typeId;

    // 최초등록일 (년년월월일일시시분분초초)
    private LocalDateTime createdTime;
    // 수정일
    private LocalDateTime modifiedTime;

    // 경도(x좌표)
    private double mapX;

    // 위도 (y좌표)
    private double mapY;
}
