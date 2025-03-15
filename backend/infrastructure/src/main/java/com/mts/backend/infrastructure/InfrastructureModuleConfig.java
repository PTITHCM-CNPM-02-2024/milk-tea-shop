package com.mts.backend.infrastructure;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScans(
        {
                @ComponentScan(basePackages = "com.mts.backend.infrastructure.product.*"),
                @ComponentScan(basePackages = "com.mts.backend.infrastructure.account.*"),
                @ComponentScan(basePackages = "com.mts.backend.infrastructure.security"),
                @ComponentScan(basePackages = "com.mts.backend.infrastructure.persistence"),
                @ComponentScan(basePackages = "com.mts.backend.infrastructure.aop")  
        }
)
@EntityScan(basePackages = {
        "com.mts.backend.infrastructure.persistence",
        "com.mts.backend.infrastructure.persistence.entity"})
@EnableJpaRepositories
public class InfrastructureModuleConfig {
}
