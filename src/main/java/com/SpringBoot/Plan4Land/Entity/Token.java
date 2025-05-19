package com.SpringBoot.Plan4Land.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Table(name="token")
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;    // 토큰 고유 ID

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    @JsonIgnore
    private Member member;  // 유저 고유 id

    @Column(nullable = false, unique = true)
    private String refreshToken; // 리프레시 토큰 값

    @Column(nullable = false)
    private LocalDateTime issuedAt; // 토큰 발급 시간

    @Column(nullable = false)
    private Long expiration; // 토큰 만료 시간

    @Column(nullable = false)
    private Boolean isActive = true; // 토큰의 활성화 상태 (Optional)

    public Token(Member member, String refreshToken, LocalDateTime issuedAt, Long expiration) {
        this.member = member;
        this.refreshToken = refreshToken;
        this.issuedAt = issuedAt;
        this.expiration = expiration;
    }
}
