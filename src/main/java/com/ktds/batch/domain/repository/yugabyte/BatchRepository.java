package com.ktds.batch.domain.repository.yugabyte;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ktds.batch.domain.entity.yugabyte.BatchInfo;

public interface BatchRepository extends JpaRepository<BatchInfo,String> {
    BatchInfo findByJobClassNm(String jobClassNm);
}
