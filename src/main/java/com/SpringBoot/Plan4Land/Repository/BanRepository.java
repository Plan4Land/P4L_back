package com.SpringBoot.Plan4Land.Repository;

import com.SpringBoot.Plan4Land.Entity.Ban;
import com.SpringBoot.Plan4Land.Entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BanRepository extends JpaRepository<Ban, Long> {
    Ban findFirstByMemberOrderByIdDesc(Member member);

    Ban findByMember(Member member);

    List<Ban> findByIsEndFalseAndEndDateBefore(LocalDateTime endDateBefore);

    boolean existsByMemberAndEndDateIsAfter(Member member, LocalDateTime endDateAfter);
}
