package com.ktds.batch.batch.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ktds.batch.batch.mapper.BatchMapper;
import com.ktds.batch.domain.dto.BatchResDto;
import com.ktds.batch.domain.entity.yugabyte.BatchInfo;
import com.ktds.batch.domain.repository.yugabyte.BatchRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BatchService {

    private final BatchRepository batchRepository;
    private final BatchMapper batchMapper;

    /**
     * 배치 정보 저장
     * @param batchInfo
     */
    @Transactional
    public void registerBatch(BatchInfo batchInfo) {
        batchRepository.save(batchInfo);
    }

    @Transactional(readOnly = true)
    public BatchInfo getBatch(String jobClassNm) {
        return batchRepository.findByJobClassNm(jobClassNm);
    }

    @Transactional(readOnly = true)
    public List<BatchResDto> getBatchList() {
        List<BatchResDto> batchList = batchRepository.findAll().stream().map(batchMapper::toBatchResDto).collect(
            Collectors.toList());
        return batchList;
    }

}
