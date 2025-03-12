package com.mts.backend.application;

import com.mts.backend.application.product.ProductCommandBus;
import com.mts.backend.application.product.handler.CreateProductInformCommandHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScans({@ComponentScan("com.mts.backend.application.product.*"),
@ComponentScan("com.mts.backend.application.*")})
public class ApplicationModuleConfig {
}
