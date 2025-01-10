package com.SpringBoot.Plan4Land.Repository;

import com.SpringBoot.Plan4Land.DTO.TravelSpotResDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.SpringBoot.Plan4Land.Entity.TravelSpot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TravelSpotRepository extends JpaRepository<TravelSpot, Long> {
    Page<TravelSpot> findAll(Pageable pageable);

    @Query(value = "select ts.*, bk.* from travel_spot ts inner join (select count(*) as bk_count, spot from bookmark_spot where spot = :spotId group by spot) bk on ts.spot_id=bk.spot", nativeQuery = true)
    TravelSpotResDto getSpotDetail(@Param("spotId") Long spotId);
}
