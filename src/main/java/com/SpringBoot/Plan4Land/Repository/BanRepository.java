package com.SpringBoot.Plan4Land.Repository;

import com.SpringBoot.Plan4Land.Entity.Ban;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BanRepository extends JpaRepository<Ban, Long> {
}
