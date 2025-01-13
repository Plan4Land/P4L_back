package com.SpringBoot.Plan4Land.Repository;

import com.SpringBoot.Plan4Land.DTO.TravelSpotResDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.SpringBoot.Plan4Land.Entity.TravelSpot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TravelSpotRepository extends JpaRepository<TravelSpot, Long> {
    Page<TravelSpot> findAll(Pageable pageable);
    Optional<TravelSpot> findById(Long spotId);
}
