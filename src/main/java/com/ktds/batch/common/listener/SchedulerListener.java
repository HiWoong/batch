package com.ktds.batch.common.listener;

import java.time.LocalDateTime;
import java.util.List;

import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.listeners.SchedulerListenerSupport;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.ktds.batch.domain.entity.postgres.BatchHistInfo;
import com.ktds.batch.batch.service.BatchHistService;
import com.ktds.batch.domain.enums.TriggerSttus;

@Component
@RequiredArgsConstructor
@Slf4j
public class SchedulerListener extends SchedulerListenerSupport {

    private final Scheduler scheduler;
    private final BatchHistService batchHistService;

    // 애플리케이션 종료 시 실행중인 Job이 있다면 실패로 저장
    @Override
    public void schedulerShuttingdown() {
        try {
            // 현재 실행 중인 JobExecutionContext 목록 조회
            List<JobExecutionContext> executingJobs = scheduler.getCurrentlyExecutingJobs();

            for (JobExecutionContext context : executingJobs) {
                JobKey jobKey = context.getJobDetail().getKey();

                // 배치 이력 상태를 FAILED 로 업데이트
                BatchHistInfo batchHistInfo = new BatchHistInfo();
                batchHistInfo.setJobClassNm(context.getJobDetail().getKey().getName())
                    .setExecutionTime(LocalDateTime.now())
                    .setSttus(
                        TriggerSttus.FAIL.getCode())
                    .setDescription(TriggerSttus.FAIL.getDescription());
                batchHistService.updateBatchHist(batchHistInfo, TriggerSttus.START.getCode());
                log.info(">>> 종료 시 실행 중이던 Job 실패 처리: {}", jobKey);
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
}
