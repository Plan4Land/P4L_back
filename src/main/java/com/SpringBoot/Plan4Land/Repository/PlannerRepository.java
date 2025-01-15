package com.SpringBoot.Plan4Land.Repository;

import com.SpringBoot.Plan4Land.Entity.Planner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlannerRepository extends JpaRepository<Planner, Long> {
    Optional<Planner> findById(Long id);
    Page<Planner> findAll(Pageable pageable);

    @Query(value = """
    SELECT p
        FROM Planner p
            WHERE (:areaCode IS NULL OR p.area = :areaCode)
            AND (:subAreaCode IS NULL OR p.subArea = :subAreaCode)
            AND (COALESCE(:themeList, NULL) IS NULL OR p.theme IN :themeList)
            AND (:searchQuery IS NULL OR p.title LIKE %:searchQuery%)
    """)
    Page<Planner> getFilteredPlanners(Pageable pageable,
                                      @Param("areaCode") String areaCode,
                                      @Param("subAreaCode") String subAreaCode,
                                      @Param("themeList") List<String> themeList,
                                      @Param("searchQuery") String searchQuery);
}
