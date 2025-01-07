package com.SpringBoot.Plan4Land.DTO;

import com.SpringBoot.Plan4Land.Entity.Member;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberResDto {
    private String id;
    private String name;
    private String imgPath;
    private LocalDateTime regDate;

    public static MemberResDto of(Member member) {
        return MemberResDto.builder()
                .name(member.getName())
                .id(member.getId())
                .imgPath(member.getProfileImg())
                .regDate(member.getSignupDate())
                .build();
    }
}