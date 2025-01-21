package com.SpringBoot.Plan4Land.DTO;

import com.SpringBoot.Plan4Land.Constant.Role;
import com.SpringBoot.Plan4Land.Entity.Member;
import lombok.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberReqDto {
    private String id;
    private String email;
    private String password;
    private String nickname;
    private String name;
    private String profileImg;
    private String socialId;
    private String sso;

    public Member toEntity(PasswordEncoder passwordEncoder) {
        return Member.builder()
                .id(id)
                .email(email)
                .password(passwordEncoder.encode(password))
                .nickname(nickname)
                .name(name)
                .profileImg(profileImg)
                .socialId(socialId)
                .sso(sso)
                .build();
    }
    public UsernamePasswordAuthenticationToken toAuthentication() {
        return new UsernamePasswordAuthenticationToken(id, password);
    }
}
