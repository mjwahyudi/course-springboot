package com.module1.demo4.Service;

import com.module1.demo4.configuration.GreetingProperties;

public class GreetingService {
    private GreetingProperties greetingProperties;

    public GreetingService(GreetingProperties greetingProperties) {
        this.greetingProperties = greetingProperties;
    }

    public String getGreetingMessage() {
        return String.format("%s, %s!", greetingProperties.getMessage(), greetingProperties.getAudience());
    }
}
