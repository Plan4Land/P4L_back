package com.SpringBoot.Plan4Land.Repository;

import com.SpringBoot.Plan4Land.Entity.Planner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface PlannerRepository extends JpaRepository<Planner, Long> {
    Optional<Planner> findById(Long id);

    Page<Planner> findAll(Pageable pageable);

    //    @Query(value = """
//            SELECT p
//                FROM Planner p
//                    WHERE (:areaCode IS NULL OR p.area = :areaCode)
//                    AND (:subAreaCode IS NULL OR p.subArea = :subAreaCode)
//                    AND (COALESCE(:themeList, NULL) IS NULL OR p.theme IN :themeList)
//                    AND (:searchQuery IS NULL OR p.title LIKE %:searchQuery%)
//            """)
//    Page<Planner> getFilteredPlanners(Pageable pageable,
//                                      @Param("areaCode") String areaCode,
//                                      @Param("subAreaCode") String subAreaCode,
//                                      @Param("themeList") List<String> themeList,
//                                      @Param("searchQuery") String searchQuery);
    @Query(value = """
            SELECT p
            FROM Planner p
            WHERE (:areaCode IS NULL OR p.area = :areaCode)
              AND (:subAreaCode IS NULL OR p.subArea = :subAreaCode)
              AND (:searchQuery IS NULL OR p.title LIKE %:searchQuery%)
              AND (
                     (:theme1 IS NULL OR p.theme LIKE %:theme1%)
                     OR (:theme2 IS NOT NULL AND p.theme LIKE %:theme2%)
                     OR (:theme3 IS NOT NULL AND p.theme LIKE %:theme3%)
                     )
            """)
    Page<Planner> getFilteredPlanners(Pageable pageable,
                                      @Param("areaCode") String areaCode,
                                      @Param("subAreaCode") String subAreaCode,
                                      @Param("searchQuery") String searchQuery,
                                      @Param("theme1") String theme1,
                                      @Param("theme2") String theme2,
                                      @Param("theme3") String theme3);



    Page<Planner> findByOwnerId(String memberId, Pageable pageable);

}
