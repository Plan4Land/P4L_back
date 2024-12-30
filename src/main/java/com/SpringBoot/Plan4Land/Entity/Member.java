package com.SpringBoot.Plan4Land.Entity;

import com.SpringBoot.Plan4Land.Constant.Role;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;

@Table(name="member")
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uid;

    private String email;

    private String password;

    private String name;

    private String nickname;

    private String phone;

    private String profileImg;

    private String sso;

    @Enumerated(EnumType.STRING)
    private Role role;


}
