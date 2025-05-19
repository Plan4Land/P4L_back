package com.SpringBoot.Plan4Land.Repository.Planner;

import com.SpringBoot.Plan4Land.DTO.PlannerResDto;
import com.SpringBoot.Plan4Land.Entity.Planner;
import com.SpringBoot.Plan4Land.Entity.QBookmarkPlanner;
import com.SpringBoot.Plan4Land.Entity.QPlanner;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class PlannerRepositoryImpl implements PlannerRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QPlanner planner = QPlanner.planner;
    private final QBookmarkPlanner bookMarkPlanner = QBookmarkPlanner.bookmarkPlanner;

    @Override
    public Page<PlannerResDto> findFilteredPlannersWithBookmarkCount(
            String areaCode, String subAreaCode, String searchQuery,
            String[] themes, String sortBy, Pageable pageable) {

        // Tuple로 결과 조회
        List<Tuple> results = queryFactory
                .select(planner, bookMarkPlanner.id.count())
                .from(planner)
                .leftJoin(bookMarkPlanner).on(bookMarkPlanner.planner.id.eq(planner.id))
                .where(
                        areaCodeEq(areaCode),
                        subAreaCodeEq(subAreaCode),
                        searchQueryContains(searchQuery),
                        themeContains(themes),
                        planner.isPublic.isTrue()
                )
                .groupBy(planner.id)
                .orderBy(getOrderSpecifier(sortBy))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 결과를 PlannerResDto로 변환
        List<PlannerResDto> content = results.stream()
                .map(tuple -> {
                    Planner plannerEntity = tuple.get(0, Planner.class);
                    Long bookmarkCount = tuple.get(1, Long.class);
                    return PlannerResDto.fromEntity(Objects.requireNonNull(plannerEntity), null, bookmarkCount);
                })
                .collect(Collectors.toList());

        // 전체 카운트 쿼리
        Long total = queryFactory
                .select(planner.id.countDistinct())
                .from(planner)
                .where(
                        areaCodeEq(areaCode),
                        subAreaCodeEq(subAreaCode),
                        searchQueryContains(searchQuery),
                        themeContains(themes),
                        planner.isPublic.isTrue()
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0 : total);
    }

    private BooleanExpression areaCodeEq(String areaCode) {
        return StringUtils.hasText(areaCode) ? planner.area.eq(areaCode) : null;
    }

    private BooleanExpression subAreaCodeEq(String subAreaCode) {
        return StringUtils.hasText(subAreaCode) ? planner.subArea.eq(subAreaCode) : null;
    }

    private BooleanExpression searchQueryContains(String searchQuery) {
        return StringUtils.hasText(searchQuery) ? planner.title.containsIgnoreCase(searchQuery) : null;
    }

    private BooleanExpression themeContains(String[] themes) {
        if (themes == null || themes.length == 0) {
            return null;
        }
        BooleanExpression expression = null;
        for (String theme : themes) {
            if (StringUtils.hasText(theme)) {
                String trimmedTheme = theme.trim();
                if (expression == null) {
                    expression = planner.theme.containsIgnoreCase(trimmedTheme);
                } else {
                    expression = expression.or(planner.theme.containsIgnoreCase(trimmedTheme));
                }
            }
        }
        return expression;
    }

    private OrderSpecifier<?> getOrderSpecifier(String sortBy) {
        PathBuilder<Planner> entityPath = new PathBuilder<>(Planner.class, "planner");

        return switch (sortBy) {
            case "LatestAsc" -> // 오래된순 (ID 오름차순)
                    new OrderSpecifier<>(Order.ASC, entityPath.getComparable("id", Long.class));
            case "BookmarkAsc" -> // 북마크 적은순
                    new OrderSpecifier<>(Order.ASC, bookMarkPlanner.id.count());
            case "BookmarkDesc" -> // 북마크 많은순
                    new OrderSpecifier<>(Order.DESC, bookMarkPlanner.id.count());
            default -> // "LatestDesc" 또는 기본값: 최신순 (ID 내림차순)
                    new OrderSpecifier<>(Order.DESC, entityPath.getComparable("id", Long.class));
        };
    }
}