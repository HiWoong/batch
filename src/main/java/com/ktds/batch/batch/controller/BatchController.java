package com.ktds.batch.batch.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import com.ktds.batch.batch.service.BatchService;
import com.ktds.batch.domain.dto.BatchResDto;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v1")
public class BatchController {

    private final BatchService batchService;

    @GetMapping("/batch-list")
    public List<BatchResDto> getBatchList() {
        return batchService.getBatchList();
    }

}
