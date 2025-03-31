package com.mts.backend.infrastructure;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScans(
        {

                @ComponentScan(basePackages = "com.mts.backend.infrastructure.security"),
                @ComponentScan(basePackages = "com.mts.backend.infrastructure.aop"),
        }
)
//@EntityScan(basePackages = {
//        "com.mts.backend.infrastructure.persistence",
//        "com.mts.backend.infrastructure.persistence.entity"})
//@EnableJpaRepositories
public class InfrastructureModuleConfig {
}
