package com.SpringBoot.Plan4Land.Repository;

import com.SpringBoot.Plan4Land.Entity.BookmarkPlanner;
import com.SpringBoot.Plan4Land.Entity.Member;
import com.SpringBoot.Plan4Land.Entity.Planner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookMarkPlannerRepository extends JpaRepository<BookmarkPlanner, Long> {
    boolean existsByMemberAndPlanner(Member member, Planner planner);
    BookmarkPlanner findByMemberAndPlanner(Member member, Planner planner);

    @Query("SELECT COUNT(bp) FROM BookmarkPlanner bp WHERE bp.planner.id = :plannerId")
    Long countByPlannerId(@Param("plannerId") Long plannerId);

    Page<BookmarkPlanner> findByMemberId(String memberId, Pageable pageable);
}
