package com.ktds.batch.jobs;

import java.time.LocalDateTime;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.ktds.batch.crawling.service.DemoService;

@Slf4j
@DisallowConcurrentExecution
@RequiredArgsConstructor
public class TestJob2 implements Job {

    private final DemoService demoService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("[Job2] 실행 시간: {}", LocalDateTime.now());
        try {
            demoService.getOpenAPIList();
        } catch (Exception e) {
            throw new JobExecutionException(e);
        }
    }
}
