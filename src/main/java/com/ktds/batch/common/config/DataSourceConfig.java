package com.ktds.batch.common.config;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSourceConfig {

    @Bean
    @ConfigurationProperties("spring.datasource.yugabyte")
    public DataSourceProperties yugabyteDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "yugabyteDataSource")
    public DataSource yugabyteDataSource() {
        return yugabyteDataSourceProperties()
            .initializeDataSourceBuilder()
            .build();
    }

    @Bean
    @ConfigurationProperties("spring.datasource.postgres")
    public DataSourceProperties postgresDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "postgresDataSource")
    public DataSource postgresDataSource() {
        return postgresDataSourceProperties()
            .initializeDataSourceBuilder()
            .build();
    }
}
