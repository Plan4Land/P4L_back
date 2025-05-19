package com.SpringBoot.Plan4Land.Repository.Planner;

import com.SpringBoot.Plan4Land.DTO.PlannerResDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface PlannerRepositoryCustom {
    Page<PlannerResDto> findFilteredPlannersWithBookmarkCount(
            String areaCode, String subAreaCode, String searchQuery,
            String[] themes, String sortBy, Pageable pageable);
}
