package com.SpringBoot.Plan4Land.Repository.Planner;

import com.SpringBoot.Plan4Land.Entity.Member;
import com.SpringBoot.Plan4Land.Entity.Planner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlannerRepository extends JpaRepository<Planner, Long>, PlannerRepositoryCustom {
    Optional<Planner> findById(Long id);

    Page<Planner> findAll(Pageable pageable);

    Page<Planner> findByOwnerId(String memberId, Pageable pageable);

    Page<Planner> findByOwnerIdAndIsPublicTrue(String memberId, Pageable pageable);


    @Query("SELECT p FROM Planner p " +
            "WHERE p.owner = :owner OR p.id IN (" +
            "SELECT pm.planner FROM PlannerMembers pm WHERE pm.member = :owner AND pm.state = 'ACCEPT')")
    Page<Planner> findPlannersByOwnerOrMember(Member owner, Pageable pageable);

    @Query("SELECT MAX(p.id) FROM Planner p")
    Long findLastId();

    List<Planner> findByIdInAndIsPublicTrue(List<Long> lst);

}
