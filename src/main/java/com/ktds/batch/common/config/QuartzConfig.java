package com.ktds.batch.common.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@Configuration
public class QuartzConfig {

    @Autowired
    private Environment env;

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(@Qualifier("yugabyteDataSource") DataSource dataSource) {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setDataSource(dataSource);

        Properties props = new Properties();
        props.put("org.quartz.scheduler.instanceId", env.getProperty("spring.quartz.properties.org.quartz.scheduler.instanceId"));
        props.put("org.quartz.threadPool.threadCount", env.getProperty("spring.quartz.properties.org.quartz.threadPool.threadCount"));
        props.put("org.quartz.jobStore.class", env.getProperty("spring.quartz.properties.org.quartz.jobStore.class"));
        props.put("org.quartz.jobStore.isClustered", env.getProperty("spring.quartz.properties.org.quartz.jobStore.isClustered"));
        props.put("org.quartz.jobStore.tablePrefix", env.getProperty("spring.quartz.properties.org.quartz.jobStore.tablePrefix"));
        props.put("org.quartz.jobStore.driverDelegateClass", env.getProperty("spring.quartz.properties.org.quartz.jobStore.driverDelegateClass"));
        props.put("org.quartz.jobStore.misfireThreshold", env.getProperty("spring.quartz.properties.org.quartz.jobStore.misfireThreshold"));

        factory.setQuartzProperties(props);

        return factory;
    }
}