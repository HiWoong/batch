package com.ktds.batch.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ktds.batch.entity.BatchInfo;

public interface BatchRepository extends JpaRepository<BatchInfo,String> {
}
