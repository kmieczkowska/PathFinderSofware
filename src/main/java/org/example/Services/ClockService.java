package org.example.Services;

import java.util.concurrent.atomic.AtomicBoolean;

public class ClockService {

    public AtomicBoolean running = new AtomicBoolean(true);

    private int counter = 0;

    public synchronized void increment() {
        counter++;
    }

    public synchronized int getCounter() {
        return counter;
    }

    public void start(){
        long startTime;
        final long TEST_DURATION_NS = 30_000_000_000L;
        startTime = System.nanoTime();
        while(System.nanoTime() - startTime < TEST_DURATION_NS);
        running.set(false);
    }


}
