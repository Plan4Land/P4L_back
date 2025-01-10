package com.SpringBoot.Plan4Land.Repository;

import com.SpringBoot.Plan4Land.Entity.PayRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PayRecordRepository extends JpaRepository<PayRecord, Long> {
}
