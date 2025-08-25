package com.ktds.batch.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ktds.batch.entity.BatchHistInfo;

public interface BatchHistRepository extends JpaRepository<BatchHistInfo, String> {
    BatchHistInfo findByJobClassNmAndSttus(String batchId, String sttus);
}
