package com.SpringBoot.Plan4Land.Repository;

import com.SpringBoot.Plan4Land.Entity.BookmarkSpot;
import com.SpringBoot.Plan4Land.Entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookMarkSpotRepository extends JpaRepository<BookmarkSpot, Long> {
    Long countBySpot(String spot);
    boolean existsByMemberIdAndSpot(String memberId, String spot);
    void deleteByMemberIdAndSpot(String memberId, String spot);

    boolean existsByMemberAndSpot(Member member, String spotId);

    Optional<BookmarkSpot> findByMemberAndSpot(Member member, String spotId);
}
