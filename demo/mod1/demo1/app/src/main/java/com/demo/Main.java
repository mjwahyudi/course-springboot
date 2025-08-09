package com.demo;

interface Engine {
    public void start();
}

class V8Engine implements Engine {
    private String type = "V8";

    public V8Engine() {
        // Constructor implementation
    }

    @Override
    public void start() {
        System.out.println("Starting the "+ type+" engine...");
    }
}

class TurboEngine implements Engine {
    private String type = "Turbo";

    public TurboEngine() {
        // Constructor implementation
    }

    @Override
    public void start() {
        System.out.println("Starting the "+ type+" engine...");
    }
}

class Car {
    private final Engine engine;

    public Car(Engine engine) {
        this.engine = engine;
    }

    public void drive(String destination) {
        engine.start();
        System.out.println("Go to " + destination);
    }
}

public class Main {
    public static void main(String[] args) {
        Car bmw = new Car(new V8Engine());
        bmw.drive("Berlin");

        bmw = new Car(new TurboEngine());
        bmw.drive("Munich");
    }
}