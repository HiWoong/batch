package com.ktds.batch.domain.repository.postgres;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ktds.batch.domain.entity.postgres.BatchHistInfo;

public interface BatchHistRepository extends JpaRepository<BatchHistInfo, String> {
    Optional<BatchHistInfo> findByJobClassNmAndSttus(String batchId, String sttus);
}
