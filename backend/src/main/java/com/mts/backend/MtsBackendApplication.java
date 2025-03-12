package com.mts.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@Import(
        {
                com.mts.backend.infrastructure.InfrastructureModuleConfig.class,
                com.mts.backend.application.ApplicationModuleConfig.class,
                com.mts.backend.api.ApiModuleConfig.class,
                com.mts.backend.infrastructure.security.WebNoSecurity.class
        }
)
@EnableAspectJAutoProxy
public class MtsBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(MtsBackendApplication.class, args);
    }
}
