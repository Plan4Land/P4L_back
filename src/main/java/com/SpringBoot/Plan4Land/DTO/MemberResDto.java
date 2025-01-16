package com.SpringBoot.Plan4Land.DTO;

import com.SpringBoot.Plan4Land.Constant.Role;
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
    private Long uid;
    private String id;
    private String email;
    private String name;
    private String nickname;
    private String imgPath;
    private LocalDateTime regDate;
    private String state;
    private Role role;

    public static MemberResDto of(Member member) {
        return MemberResDto.builder()
                .uid(member.getUid())
                .id(member.getId())
                .email(member.getEmail())
                .name(member.getName())
                .nickname(member.getNickname())
                .imgPath(member.getProfileImg())
                .regDate(member.getSignUpDate())
                .role(member.getRole())
                .build();
    }

    public static MemberResDto of(Member member, String state) {
        return MemberResDto.builder()
                .uid(member.getUid())
                .id(member.getId())
                .email(member.getEmail())
                .name(member.getName())
                .nickname(member.getNickname())
                .imgPath(member.getProfileImg())
                .regDate(member.getSignUpDate())
                .state(state)
                .role(member.getRole())
                .build();
    }
}