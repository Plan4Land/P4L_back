package com.SpringBoot.Plan4Land.Repository;

import com.SpringBoot.Plan4Land.Entity.BookmarkSpot;
import com.SpringBoot.Plan4Land.Entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookMarkSpotRepository extends JpaRepository<BookmarkSpot, Long> {

    boolean existsByMemberAndSpot(Member member, String spotId);

    Optional<BookmarkSpot> findByMemberAndSpot(Member member, String spotId);

    int countBySpot(String spotId);

    List<BookmarkSpot> findByMember(Member member);

    Page<BookmarkSpot> findByMemberId(Long memberId, Pageable pageable);
}
