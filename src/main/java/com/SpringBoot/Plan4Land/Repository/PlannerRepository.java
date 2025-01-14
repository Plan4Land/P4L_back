package com.SpringBoot.Plan4Land.Repository;

import com.SpringBoot.Plan4Land.Entity.Planner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlannerRepository extends JpaRepository<Planner, Long> {
    Optional<Planner> findById(Long id);
    Page<Planner> findAll(Pageable pageable);
}
