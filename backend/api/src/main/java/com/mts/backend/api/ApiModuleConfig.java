package com.mts.backend.api;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScans(
        {
                @ComponentScan("com.mts.backend.api.product.*"),
                @ComponentScan("com.mts.backend.api.account.*"),
                @ComponentScan("com.mts.backend.api.staff.*"),
                @ComponentScan("com.mts.backend.api.store.*"),
                @ComponentScan("com.mts.backend.api.promotion.*"),
        }
)
public class ApiModuleConfig {
}
