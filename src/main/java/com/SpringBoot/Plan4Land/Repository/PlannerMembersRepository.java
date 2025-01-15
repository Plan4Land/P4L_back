package com.SpringBoot.Plan4Land.Repository;

import com.SpringBoot.Plan4Land.Entity.Member;
import com.SpringBoot.Plan4Land.Entity.Planner;
import com.SpringBoot.Plan4Land.Entity.PlannerMembers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlannerMembersRepository extends JpaRepository<PlannerMembers, Long> {
    List<PlannerMembers> findByPlannerId(Long plannerId);
    Optional<PlannerMembers> findByMemberIdAndPlannerId(String memberId, Long plannerId);
}
