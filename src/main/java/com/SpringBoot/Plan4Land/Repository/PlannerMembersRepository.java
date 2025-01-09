package com.SpringBoot.Plan4Land.Repository;

import com.SpringBoot.Plan4Land.Entity.PlannerMembers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlannerMembersRepository extends JpaRepository<PlannerMembers, Long> {
    List<PlannerMembers> findByPlannerId(Long plannerId);
}
