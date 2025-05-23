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

import java.util.List;

@Repository
public interface BookMarkPlannerRepository extends JpaRepository<BookmarkPlanner, Long> {
    boolean existsByMemberAndPlanner(Member member, Planner planner);
    BookmarkPlanner findByMemberAndPlanner(Member member, Planner planner);
    void deleteByPlannerId(Long plannerId);

    @Query("SELECT COUNT(bp) FROM BookmarkPlanner bp WHERE bp.planner.id = :plannerId")
    Long countByPlannerId(@Param("plannerId") Long plannerId);

    Page<BookmarkPlanner> findByMemberId(String memberId, Pageable pageble);

    @Query("SELECT bp.planner.id FROM BookmarkPlanner bp WHERE bp.member = :member")
    List<Long> findBookmarkPlannersByMember(Member member);

    @Query("SELECT b.planner.id " +
            "FROM BookmarkPlanner b " +
            "WHERE b.planner.isPublic = true " +
            "GROUP BY b.planner.id ORDER BY COUNT(b.planner.id) DESC")
    List<Long> findTop3PlannerIdsByBookmarkCount();

    @Query(value = """
            SELECT bp.planner
             FROM BookmarkPlanner bp
             WHERE bp.planner.isPublic = true
             GROUP BY bp.planner.id
             ORDER BY COUNT(bp.planner.id) DESC
            """)
    List<Planner> findTopPlanners(Pageable pageable);




}
