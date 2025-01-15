package com.SpringBoot.Plan4Land.Repository;

import com.SpringBoot.Plan4Land.Entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findById(String id);
    Optional<Member> findByNickname(String nickname);
    Optional<Member> findByEmail(String email);
    Optional<Member> findByIdAndPassword(String id, String password);
    Optional<Member> findByNameAndEmail(String name, String email);
    Optional<Member> findByIdAndEmail(String id, String email);
    @Query("SELECT m FROM Member m WHERE m.id LIKE %:id% OR m.nickname LIKE %:nickname%")
    Optional<Member> findByKakaoId(Long kakaoId);
    List<Member> findByIdOrNickname(@Param("id") String id, @Param("nickname") String nickname);

    boolean existsById(String userId);
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
}
