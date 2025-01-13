package com.SpringBoot.Plan4Land.Repository;

import com.SpringBoot.Plan4Land.DTO.TravelSpotResDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.SpringBoot.Plan4Land.Entity.TravelSpot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

import java.util.Optional;

@Repository
public interface TravelSpotRepository extends JpaRepository<TravelSpot, Long> {
    Page<TravelSpot> findAll(Pageable pageable);

    @Query(value = """ 
            SELECT * FROM travel_spot
                     WHERE (:areaCode IS NULL OR area_code = :areaCode)
                       AND (:subAreaCode IS NULL OR sigungu_code = :subAreaCode)
                       AND (:topTheme IS NULL OR cat1 = :topTheme)
                       AND (:middleTheme IS NULL OR cat2 = :middleTheme)
                       AND (:cat IS NULL OR type_id = :cat)
                       AND (:bottomThemes IS NULL OR cat3 IN :bottomThemes)
            """,
            countQuery = """ 
                    SELECT COUNT(*)
                                FROM travel_spot
                                            WHERE (:areaCode IS NULL OR area_code = :areaCode)
                                                          AND (:subAreaCode IS NULL OR sigungu_code = :subAreaCode)
                                                          AND (:topTheme IS NULL OR cat1 = :topTheme)
                                                          AND (:middleTheme IS NULL OR cat2 = :middleTheme)
                                                          AND (:cat IS NULL OR type_id = :cat)
                                                          AND (:bottomThemes IS NULL OR cat3 IN :bottomThemes)""",
            nativeQuery = true)
    Page<TravelSpot> getFilterTravelSpot(Pageable pageable,
                                         @Param("areaCode") Integer areaCode, @Param("subAreaCode") Integer subAreaCode,
                                         @Param("topTheme") String topTheme, @Param("middleTheme") String middleTheme,
                                         @Param("bottomThemes") List<String> bottomThemes,
                                         @Param("cat") String cat);


    Optional<TravelSpot> findById(Long spotId);
}
