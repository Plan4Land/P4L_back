package com.SpringBoot.Plan4Land.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.SpringBoot.Plan4Land.Entity.TravelSpot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TravelSpotRepository extends JpaRepository<TravelSpot, Long> {
    Page<TravelSpot> findAll(Pageable pageable);
}
