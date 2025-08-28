package com.ktds.batch.common.listener;

import java.time.LocalDateTime;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import com.ktds.batch.domain.entity.postgres.BatchHistInfo;
import com.ktds.batch.service.BatchHistService;
import com.ktds.batch.domain.enums.TriggerSttus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class JobListener implements org.quartz.JobListener {

    private final BatchHistService batchHistService;

    @Override
    public String getName() {
        return "TestJobListener";
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        log.info(">>> [{}] 시작 시간: {}", context.getJobDetail().getKey(), LocalDateTime.now());

        BatchHistInfo batchHistInfo = new BatchHistInfo();
        batchHistInfo.setJobClassNm(context.getJobDetail().getKey().getName()).setExecutionTime(LocalDateTime.now()).setSttus(
            TriggerSttus.START.getCode()).setDescription(TriggerSttus.START.getDescription());
        batchHistService.registerBatchHist(batchHistInfo);
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
        log.info(">>> [{}] 실행이 거부되었습니다.", context.getJobDetail().getKey());

        BatchHistInfo batchHistInfo = new BatchHistInfo();
        batchHistInfo.setJobClassNm(context.getJobDetail().getKey().getName()).setExecutionTime(LocalDateTime.now()).setSttus(
            TriggerSttus.REJECT.getCode()).setDescription(TriggerSttus.REJECT.getDescription());
        batchHistService.registerBatchHist(batchHistInfo);
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException e) {
        log.info(">>> [{}] 종료 시간: {}", context.getJobDetail().getKey(), LocalDateTime.now());

        BatchHistInfo batchHistInfo = new BatchHistInfo();
        batchHistInfo.setJobClassNm(context.getJobDetail().getKey().getName()).setExecutionTime(LocalDateTime.now());
        if (e != null) {
            batchHistInfo.setSttus(TriggerSttus.ERROR.getCode()).setDescription(TriggerSttus.ERROR.getDescription());
            log.info(">>> 에러 발생: {}", e.getMessage());
        } else {
            batchHistInfo.setSttus(TriggerSttus.END.getCode()).setDescription(TriggerSttus.END.getDescription());
            log.info(">>> 정상 종료: {}", context.getJobDetail().getKey());
        }
        batchHistService.updateBatchHist(batchHistInfo, TriggerSttus.START.getCode());
    }
}
