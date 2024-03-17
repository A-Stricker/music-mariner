package com.amandastricker.musicmariner.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application-secret.properties")
public class AppConfig {
    // Additional configuration or beans can go here
}
