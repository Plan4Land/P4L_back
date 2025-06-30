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

    //    @Query(value = """
//            SELECT * FROM travel_spot
//                     WHERE (:areaCode IS NULL OR area_code = :areaCode)
//                       AND (:subAreaCode IS NULL OR sigungu_code = :subAreaCode)
//                       AND (:topTheme IS NULL OR cat1 = :topTheme)
//                       AND (:middleTheme IS NULL OR cat2 = :middleTheme)
//                       AND (:cat IS NULL OR type_id = :cat)
//                       AND (:bottomThemes IS NULL OR cat3 IN (:bottomThemes))
//            """,
//            nativeQuery = true)
//    Page<TravelSpot> getFilterTravelSpot(Pageable pageable,
//                                         @Param("areaCode") Integer areaCode, @Param("subAreaCode") Integer subAreaCode,
//                                         @Param("topTheme") String topTheme, @Param("middleTheme") String middleTheme,
//                                         @Param("bottomThemes") List<String> bottomThemes,
//                                         @Param("cat") String cat);
    @Query(value = """ 
            SELECT t
                        FROM TravelSpot t
                        WHERE (:areaCode IS NULL OR t.areaCode = :areaCode)
                        AND (:subAreaCode IS NULL OR t.sigunguCode = :subAreaCode)
                        AND (:topTheme IS NULL OR t.cat1 = :topTheme)
                        AND (:middleTheme IS NULL OR t.cat2 = :middleTheme)
                        AND (:cat IS NULL OR t.typeId = :cat)
                        AND (COALESCE(:bottomThemes, NULL) IS NULL OR t.cat3 IN :bottomThemes)
                        AND (:searchQuery IS NULL OR t.title LIKE %:searchQuery%)
            """)
    Page<TravelSpot> getFilterTravelSpot(Pageable pageable,
                                         @Param("areaCode") Integer areaCode, @Param("subAreaCode") Integer subAreaCode,
                                         @Param("topTheme") String topTheme, @Param("middleTheme") String middleTheme,
                                         @Param("bottomThemes") List<String> bottomThemes, @Param("cat") String cat,
                                         @Param("searchQuery") String searchQuery);

    Optional<TravelSpot> findById(Long spotId);

    @Query("SELECT t FROM TravelSpot t WHERE " +
            "(6371 * acos(cos(radians(:mapY)) * cos(radians(t.mapY)) * cos(radians(t.mapX) - radians(:mapX)) + sin(radians(:mapY)) * sin(radians(t.mapY)))) < :radius " +
            "AND t.id <> :spotId") // spotId로 변경
    List<TravelSpot> findNearbySpotsExcludingId(@Param("mapX") double mapX, @Param("mapY") double mapY, @Param("radius") double radius, @Param("spotId") Long spotId);

}
