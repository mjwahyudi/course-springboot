package com.module1.demo4.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.module1.demo4.Service.GreetingService;

@Configuration
@EnableConfigurationProperties(GreetingProperties.class)
@ConditionalOnProperty(prefix = "greeting", name = "enabled", havingValue = "true", matchIfMissing = false)
public class GreetingConfig {

    @Bean
    public GreetingService greetingService(GreetingProperties greetingProperties) {
        return new GreetingService(greetingProperties);
    }

}
