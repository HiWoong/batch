package com.ktds.batch.common.config;

import java.util.Set;

import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.spi.JobFactory;
import org.reflections.Reflections;

import com.ktds.batch.domain.entity.yugabyte.BatchInfo;
import com.ktds.batch.batch.service.BatchService;

import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class SchedulerConfig {

    private final Scheduler scheduler;
    private final JobFactory jobFactory;
    private final BatchService batchService;

    @PostConstruct
    public void registerAnnotatedJobs() throws SchedulerException {
        scheduler.setJobFactory(jobFactory);

        Reflections reflections = new Reflections("com.ktds.batch.jobs");
        Set<Class<? extends Job>> jobClasses = reflections.getSubTypesOf(Job.class);

        for (Class<? extends Job> jobClass : jobClasses) {
            BatchInfo batchInfo = batchService.getBatch(jobClass.getSimpleName());

            if (batchInfo != null) {
                String cron = batchInfo.getCronExpression();
                String jobName = jobClass.getSimpleName();

                JobDetail jobDetail = JobBuilder.newJob(jobClass)
                    .withIdentity(jobName)
                    .storeDurably()
                    .build();

                Trigger trigger = TriggerBuilder.newTrigger()
                    .forJob(jobDetail)
                    .withIdentity(jobName + "Trigger")
                    .withSchedule(CronScheduleBuilder.cronSchedule(cron))
                    .build();

                try {
                    if (!scheduler.checkExists(jobDetail.getKey())) {
                        scheduler.scheduleJob(jobDetail, trigger);
                        log.info(">>> Job 등록 완료: {} {}", jobName, cron);
                    }
                } catch (SchedulerException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
