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
import org.reflections.Reflections;

import com.ktds.batch.entity.BatchInfo;
import com.ktds.batch.service.BatchService;
import com.ktds.batch.util.ScheduledCron;
import com.ktds.batch.util.enums.BatchSttus;

import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class QuartzConfig {

    private final Scheduler scheduler;
    private final BatchService batchService;

    @PostConstruct
    public void registerAnnotatedJobs() {

        Reflections reflections = new Reflections("com.ktds.batch.jobs");
        Set<Class<? extends Job>> jobClasses = reflections.getSubTypesOf(Job.class);

        for (Class<? extends Job> jobClass : jobClasses) {
            ScheduledCron annotation = jobClass.getAnnotation(ScheduledCron.class);

            if (annotation != null) {
                String cron = annotation.value();
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

                        BatchInfo batchInfo = new BatchInfo();
                        batchInfo.setNm(jobName + "Batch").setJobClassNm(jobName).setTriggerNm(jobName + "Trigger").setSttus(BatchSttus.AVAILABLE.getCode()).setCronExpression(cron).setContent(
                            "log.info(\"[Job] 실행 시간: {}\", LocalDateTime.now());\n"
                            + "        try {\n"
                            + "            Thread.sleep(5000);\n"
                            + "        } catch (InterruptedException e) {\n"
                            + "            throw new JobExecutionException(e);\n"
                            + "        }");
                        batchService.registerBatch(batchInfo);

                        System.out.printf(">>> Job 등록 완료: %s (%s)%n", jobName, cron);
                    }
                } catch (SchedulerException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
