package com.ktds.batch.jobs;

import java.time.LocalDateTime;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import lombok.extern.slf4j.Slf4j;

import com.ktds.batch.util.ScheduledCron;

@ScheduledCron("0 * * * * ?")
@Slf4j
public class TestJob1 implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("[Job1] 실행 시간: {}", LocalDateTime.now());
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new JobExecutionException(e);
        }
    }
}
