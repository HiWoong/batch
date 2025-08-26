package com.ktds.batch.common.config;

import org.quartz.Scheduler;
import org.springframework.context.annotation.Configuration;

import com.ktds.batch.jobs.TestJobListener;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class QuartzListenerConfig {

    private final Scheduler scheduler;
    private final TestJobListener testJobListener;

    @PostConstruct
    public void registerListeners() throws Exception {
        scheduler.getListenerManager().addJobListener(testJobListener);
    }

}
