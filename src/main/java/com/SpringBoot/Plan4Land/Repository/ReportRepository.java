package com.SpringBoot.Plan4Land.Repository;

import com.SpringBoot.Plan4Land.Constant.State;
import com.SpringBoot.Plan4Land.Entity.Member;
import com.SpringBoot.Plan4Land.Entity.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

// 기타 import 생략

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    @Query(value = """
        SELECT r FROM Report r
        ORDER BY
            CASE
                WHEN r.state = com.SpringBoot.Plan4Land.Constant.State.WAIT THEN 0
                WHEN r.state = com.SpringBoot.Plan4Land.Constant.State.ACCEPT THEN 1
                WHEN r.state = com.SpringBoot.Plan4Land.Constant.State.REJECT THEN 2
                ELSE 3
            END ASC,
            r.reportDate DESC
        """, countQuery = "SELECT COUNT(r) FROM Report r")
    Page<Report> findAllOrderByStateAndReportDate(Pageable pageable);

    @Query(value = """
        SELECT r FROM Report r
        WHERE r.state = :state
          AND (r.content LIKE %:keyword%
               OR r.reported.id LIKE %:keyword%
               OR r.reporter.id LIKE %:keyword%)
        ORDER BY
            CASE
                WHEN r.state = com.SpringBoot.Plan4Land.Constant.State.WAIT THEN 0
                WHEN r.state = com.SpringBoot.Plan4Land.Constant.State.ACCEPT THEN 1
                WHEN r.state = com.SpringBoot.Plan4Land.Constant.State.REJECT THEN 2
                ELSE 3
            END ASC,
            r.reportDate DESC
        """,
    countQuery = """
        SELECT COUNT(r) FROM Report r
        WHERE r.state = :state
          AND (r.content LIKE %:keyword%
               OR r.reported.id LIKE %:keyword%
               OR r.reporter.id LIKE %:keyword%)
        """)
    Page<Report> findReportSelectAndKeyword(Pageable pageable,
                                            String keyword,
                                            State state);

    @Query(value = """
        SELECT r FROM Report r
        WHERE r.content LIKE %:keyword%
               OR r.reported.id LIKE %:keyword%
               OR r.reporter.id LIKE %:keyword%
        ORDER BY
            CASE
                WHEN r.state = com.SpringBoot.Plan4Land.Constant.State.WAIT THEN 0
                WHEN r.state = com.SpringBoot.Plan4Land.Constant.State.ACCEPT THEN 1
                WHEN r.state = com.SpringBoot.Plan4Land.Constant.State.REJECT THEN 2
                ELSE 3
            END ASC,
            r.reportDate DESC
        """,
            countQuery = """
                    SELECT COUNT(r) FROM Report r
                    WHERE r.state = :state
                      AND (r.content LIKE %:keyword%
                           OR r.reported.id LIKE %:keyword%
                           OR r.reporter.id LIKE %:keyword%)
                    """)
    Page<Report> findReportByKeyword(Pageable pageable,
                                            String keyword);
    @Query(value = """
        SELECT r FROM Report r
        WHERE r.state = :state
        ORDER BY
            CASE
                WHEN r.state = com.SpringBoot.Plan4Land.Constant.State.WAIT THEN 0
                WHEN r.state = com.SpringBoot.Plan4Land.Constant.State.ACCEPT THEN 1
                WHEN r.state = com.SpringBoot.Plan4Land.Constant.State.REJECT THEN 2
                ELSE 3
            END ASC,
            r.reportDate DESC
        """,
            countQuery = """
                    SELECT COUNT(r) FROM Report r
                    WHERE r.state = :state
                    """)
    Page<Report> findReportByState(Pageable pageable, State state);

    int countReportByReported(Member reported);

    int countReportByReportedAndStateIsNot(Member reported, State state);
}
