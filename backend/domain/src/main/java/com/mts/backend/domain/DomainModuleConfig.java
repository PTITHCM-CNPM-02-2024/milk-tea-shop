package com.mts.backend.domain;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScans(
        {@ComponentScan("com.mts.backend.domain.*"),
                @ComponentScan("com.mts.backend.domain.persistence.entity"),
                @ComponentScan("com.mts.backend.domain.persistence.*"),
                @ComponentScan("com.mts.backend.domain.store.*"),
                @ComponentScan("com.mts.backend.domain.customer.*"),
                @ComponentScan("com.mts.backend.domain.product.*"),
                @ComponentScan("com.mts.backend.domain.staff.*"),
                @ComponentScan("com.mts.backend.domain.promotion.*"),
                @ComponentScan("com.mts.backend.domain.account.*"),
                @ComponentScan("com.mts.backend.domain.order.*"),
        }
)
@EntityScan(basePackages = {
        "com.mts.backend.domain.*",
        "com.mts.backend.domain.store.*",
        "com.mts.backend.domain.customer.*",
        "com.mts.backend.domain.product.*",
        "com.mts.backend.domain.staff.*",
        "com.mts.backend.domain.promotion.*",
        "com.mts.backend.domain.account.*",
        "com.mts.backend.domain.order.*",
        "com.mts.backend.domain.payment.*",
        "com.mts.backend.domain.billing.*"})
@EnableJpaRepositories
public class DomainModuleConfig {
}
