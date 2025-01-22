package com.SpringBoot.Plan4Land.Repository;

import com.SpringBoot.Plan4Land.Entity.Member;
import com.SpringBoot.Plan4Land.Entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {


    @Query(value = """
            SELECT * FROM report
                     ORDER BY
                         CASE state
                             WHEN 'wait' THEN 0
                             WHEN 'accept' THEN 1
                             WHEN 'reject' THEN 2
                             ELSE 3 END
                    , report_date
            """, nativeQuery = true)
    List<Report> findAllOrderByStateAndReportDate();

    int countReportByReported(Member reported);
}
