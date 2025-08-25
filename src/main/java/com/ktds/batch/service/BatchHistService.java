package com.ktds.batch.service;

import org.springframework.stereotype.Service;

import com.ktds.batch.entity.BatchHistInfo;
import com.ktds.batch.repository.BatchHistRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BatchHistService {

    private final BatchHistRepository batchHistRepository;

    /**
     * 배치 이력 정보 저장
     * @param batchHistInfo
     */
    public void registerBatchHist(BatchHistInfo batchHistInfo) {
        batchHistRepository.save(batchHistInfo);
    }

    /**
     * 실행 클래스 이름과 상태로 배치 이력 조회
     * @param batchId
     * @param sttus
     * @return
     */
    public BatchHistInfo findByJobClassNmAndSttus(String batchId, String sttus) {
        return batchHistRepository.findByJobClassNmAndSttus(batchId, sttus);
    }

    /**
     * 배치 이력 수정
     * @param batchHistInfo
     */
    public void updateBatchHist(BatchHistInfo batchHistInfo) {
        batchHistRepository.save(batchHistInfo);
    }

}
