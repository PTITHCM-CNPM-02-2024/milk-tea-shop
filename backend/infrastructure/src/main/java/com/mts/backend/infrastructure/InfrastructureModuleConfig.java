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
public class InfrastructureModuleConfig {
}
