package com.demo.app;

import org.springframework.stereotype.Component;

@Component
public class Car {
    private Engine engine;

    public Car(Engine engine) { // Constructor injection
        this.engine = engine;
    }

    public void setEngine(Engine engine) { // Setter injection
        this.engine = engine;
    }

    public void drive(String destination) {
        engine.start();
        System.out.println("Go to " + destination);
    }
}
