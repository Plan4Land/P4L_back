package com.SpringBoot.Plan4Land.Entity;

import com.SpringBoot.Plan4Land.Constant.Role;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Table(name="member")
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Member {
    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uid;

    private String email;

    private String password;

    private String name;

    private String nickname;

    private String phone;

    private String profileImg;

    // 제3자인증 분류
    private String sso;

    // 일반, 멤버십, 관리자
    @Enumerated(EnumType.STRING)
    private Role role;

    // 가입일
    private LocalDateTime signupDate;
    @PrePersist
    protected void onCreate() {
        this.signupDate = LocalDateTime.now();
    }

    // 탈퇴일
    private LocalDateTime signOutDate;

    // 계정 활성화 여부
    private boolean activate;
}
