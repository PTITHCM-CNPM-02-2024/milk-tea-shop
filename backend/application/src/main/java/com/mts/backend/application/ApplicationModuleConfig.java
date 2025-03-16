package com.mts.backend.application;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScans({@ComponentScan("com.mts.backend.application.product.*"),
        @ComponentScan("com.mts.backend.application.account.*"),
        @ComponentScan("com.mts.backend.application.staff.*"),
        @ComponentScan("com.mts.backend.application.*")})
public class ApplicationModuleConfig {
}
