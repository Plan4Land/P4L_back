package com.SpringBoot.Plan4Land.Entity;

import com.SpringBoot.Plan4Land.Constant.Role;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Table(name="member")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Member {
    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uid;

    @Column(unique = true, nullable = false)
    private String id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String nickname;

    private String profileImg;

    // 제3자인증 분류
    private String sso;

    // 카카오 아이디
    private String socialId;

    // 일반, 멤버십, 관리자
    @Enumerated(EnumType.STRING)
    private Role role;

    // 가입일
    private LocalDateTime signUpDate;

    // 탈퇴일
    private LocalDateTime signOutDate;

    // 계정 활성화 여부
    private boolean activate;

    // 회원가입시 데이터 자동 입력
    @PrePersist
    protected void onCreate() {
        this.signUpDate = LocalDateTime.now();
        this.activate = true;
        this.role = Role.ROLE_GENERAL;
    }

    @Builder
    public Member(String id, String email, String password, String nickname, String name, String profileImg, Role role, String socialId, String sso) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.name = name;
        this.profileImg = profileImg;
        this.signUpDate = LocalDateTime.now();
        this.socialId = socialId;
        this.sso = sso;
    }

    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Ban> bans = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<BookmarkPlanner> bookmarkPlanners = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<BookmarkSpot> bookmarkSpots = new ArrayList<>();

    @OneToMany(mappedBy = "sender", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ChatMsg> chatMsgs = new ArrayList<>();

    @OneToMany(mappedBy = "follower", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Follow> follows = new ArrayList<>();

    @OneToMany(mappedBy = "followed", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Follow> follows1 = new ArrayList<>();

    @OneToMany(mappedBy = "owner", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Planner> planners = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<PlannerMembers> plannerMembers = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Token> tokens = new ArrayList<>();

    @OneToMany(mappedBy = "reporter", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Report> reporter = new ArrayList<>();

    @OneToMany(mappedBy = "reported", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Report> reported = new ArrayList<>();

}
