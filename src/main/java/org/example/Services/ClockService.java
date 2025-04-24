package org.example.Services;

import java.util.concurrent.atomic.AtomicBoolean;

public class ClockService {

    private Thread clockHandler;

    public AtomicBoolean running = new AtomicBoolean(true);

    private int counter = 0;

    public synchronized void increment() {
        counter++;
    }

    public synchronized int getCounter() {
        return counter;
    }
    public synchronized void resetCounter() {
        counter = 0;
    }

    public void start(long TEST_DURATION_NS ) {
        running.set(true);
        clockHandler = new Thread(() -> {
            long startTime;
            startTime = System.nanoTime();
            while (System.nanoTime() - startTime < TEST_DURATION_NS);
            running.set(false);
            System.out.println("# Clock Times UP!");

        });

        clockHandler.start();
    }

    public void join() throws InterruptedException {
        if (clockHandler != null) {
            clockHandler.join(); // 🧵 Wait for thread to finish
        }
    }


}
