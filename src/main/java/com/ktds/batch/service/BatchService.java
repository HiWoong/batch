package com.ktds.batch.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ktds.batch.domain.entity.yugabyte.BatchInfo;
import com.ktds.batch.domain.repository.yugabyte.BatchRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BatchService {

    private final BatchRepository batchRepository;

    /**
     * 배치 정보 저장
     * @param batchInfo
     */
    @Transactional
    public void registerBatch(BatchInfo batchInfo){
        batchRepository.save(batchInfo);
    }

    @Transactional(readOnly = true)
    public BatchInfo getBatchInfo(String jobClassNm) {
        return batchRepository.findByJobClassNm(jobClassNm);
    }

}
