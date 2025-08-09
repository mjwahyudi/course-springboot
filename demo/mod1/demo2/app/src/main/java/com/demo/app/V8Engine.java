package com.demo.app;

import org.springframework.stereotype.Component;

@Component // Marks this class as a Spring-managed bean
public class V8Engine implements Engine {
    public V8Engine() {
        // Constructor implementation
    }

    @Override
    public void start() {
        System.out.println("Starting the V8 engine...");
    }
}
