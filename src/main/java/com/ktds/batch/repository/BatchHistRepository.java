package com.ktds.batch.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ktds.batch.entity.BatchHistInfo;

public interface BatchHistRepository extends JpaRepository<BatchHistInfo, String> {
    Optional<BatchHistInfo> findByJobClassNmAndSttus(String batchId, String sttus);
}
