package com.ktds.batch.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.ktds.batch.domain.entity.postgres.BatchHistInfo;
import com.ktds.batch.domain.repository.postgres.BatchHistRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BatchHistService {

    private final BatchHistRepository batchHistRepository;

    /**
     * 배치 이력 정보 저장
     * @param batchHistInfo
     */
    @Transactional
    public BatchHistInfo registerBatchHist(BatchHistInfo batchHistInfo) {
        return batchHistRepository.save(batchHistInfo);
    }

    /**
     * 배치 이력 정보 수정
     * @param batchHistInfo
     */
    @Transactional
    public void updateBatchHist(BatchHistInfo batchHistInfo, String sttusCode) {
        BatchHistInfo batchHist = this.findByJobClassNmAndSttus(batchHistInfo.getJobClassNm(),
            sttusCode);
        batchHist.setExecutionTime(batchHistInfo.getExecutionTime())
            .setSttus(batchHistInfo.getSttus())
            .setDescription(batchHistInfo.getDescription());
        this.registerBatchHist(batchHist);
    }

    /**
     * 실행 클래스 이름과 상태로 배치 이력 조회
     * @param batchId
     * @param sttus
     * @return
     */
    @Transactional(readOnly = true)
    protected BatchHistInfo findByJobClassNmAndSttus(String batchId, String sttus) {
        return batchHistRepository.findByJobClassNmAndSttus(batchId, sttus)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "BatchHistInfo findByJobClassNmAndSttus"
            ));
    }

}
