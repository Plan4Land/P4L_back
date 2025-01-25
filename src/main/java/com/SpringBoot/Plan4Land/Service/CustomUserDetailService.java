package com.SpringBoot.Plan4Land.Service;

import com.SpringBoot.Plan4Land.Entity.Member;
import com.SpringBoot.Plan4Land.Repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        Member member = memberRepository.findById(id).orElseThrow(()
                -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + id));
        return createUserDetail(member);
    }

    private UserDetails createUserDetail(Member member) {
        String role = member.getRole() != null ? member.getRole().toString() : "ROLE_GENERAL";
        log.warn("역할 : {} ", role);
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(role);

        return new User(member.getId(), member.getPassword(), Collections.singleton(grantedAuthority));
    }
}
