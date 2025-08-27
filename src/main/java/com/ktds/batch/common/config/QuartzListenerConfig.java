package com.ktds.batch.common.config;

import org.quartz.Scheduler;
import org.springframework.context.annotation.Configuration;

import com.ktds.batch.common.listener.SchedulerListener;
import com.ktds.batch.common.listener.JobListener;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class QuartzListenerConfig {

    private final Scheduler scheduler;
    private final JobListener jobListener;
    private final SchedulerListener schedulerListener;

    @PostConstruct
    public void registerListeners() throws Exception {
        scheduler.getListenerManager().addJobListener(jobListener);
        scheduler.getListenerManager().addSchedulerListener(schedulerListener);
    }

}
