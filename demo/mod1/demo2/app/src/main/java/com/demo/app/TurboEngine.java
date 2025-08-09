package com.demo.app;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary  // Spring will inject this if multiple beans match
public class TurboEngine implements Engine {

    public TurboEngine() {
        // Constructor implementation
    }

    @Override
    public void start() {
        System.out.println("Starting the Turbo engine...");
    }
}