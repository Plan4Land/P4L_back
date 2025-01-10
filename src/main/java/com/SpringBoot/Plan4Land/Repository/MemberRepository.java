package com.SpringBoot.Plan4Land.Repository;

import com.SpringBoot.Plan4Land.Entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findById(String id);
    Optional<Member> findByIdAndPassword(String id, String password);
    Optional<Member> findByNameAndEmail(String name, String email);
    Optional<Member> findByIdAndEmail(String id, String email);
    boolean existsById(String userId);
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);

}
