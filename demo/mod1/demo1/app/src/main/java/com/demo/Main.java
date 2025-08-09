package com.demo;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.demo") // Automatically detects @Component classes
public class Main {
    public static void main(String[] args) {
        try (var context = new AnnotationConfigApplicationContext(Main.class)) {
            Car car = context.getBean(Car.class);
            car.drive("Berlin");

            Engine engine = context.getBean(V8Engine.class);
            car.setEngine(engine);
            car.drive("Munich");
            
        }
    }
}