package com.mts.backend.infrastructure.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "jwt-config")
@Data
public class JwtProperties {
    private String secret;
    private String issuer;
    private String header;
    private String prefix;
    private Long expiration;
}
