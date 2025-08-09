package com.demo;

class Engine {
    private String type = "V8";

    public Engine() {
        // Constructor implementation
    }

    public void start() {
        System.out.println("Starting the "+ type+" engine...");
    }
}

class Car {
    private Engine engine = new Engine();

    public Car() {
        // Constructor implementation
    }

    public void drive(String destination) {
        engine.start();
        System.out.println("Go to " + destination);
    }
}

public class Main {
    public static void main(String[] args) {
        Car bmw = new Car();
        bmw.drive("Berlin");
    }
}