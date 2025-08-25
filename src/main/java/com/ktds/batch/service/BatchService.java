package com.ktds.batch.service;

import org.springframework.stereotype.Service;

import com.ktds.batch.entity.BatchInfo;
import com.ktds.batch.repository.BatchRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BatchService {

    private final BatchRepository batchRepository;

    public void save(BatchInfo batchInfo){
        batchRepository.save(batchInfo);
    }

}
