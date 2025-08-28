package com.ktds.batch.common.config;

import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class QuartzJobFactoryConfig {

    private final AutowireCapableBeanFactory beanFactory;

    @Bean
    public JobFactory springBeanJobFactory() {
        return new SpringBeanJobFactory() {
            @Override
            protected Object createJobInstance(final TriggerFiredBundle bundle) throws Exception {
                final Object job = super.createJobInstance(bundle);
                beanFactory.autowireBean(job);
                return job;
            }
        };
    }
}