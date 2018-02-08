package com.robinjonsson.dwquartz;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class LatchJob extends AbstractJob {

    private final CountDownLatch latch = new CountDownLatch(1);
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final AtomicBoolean complete = new AtomicBoolean(false);

    public LatchJob(final String testName) {
        super(testName);
    }

    @Override
    protected void executeJob(final JobExecutionContext context) throws JobExecutionException {
        running.set(true);
        try {
            latch.await(10, TimeUnit.SECONDS);
            complete.set(true);
        } catch (final InterruptedException e) {
            throw new JobExecutionException(e);
        } finally {
            running.set(false);
        }
    }

    public CountDownLatch getLatch() {
        return latch;
    }

    public boolean isRunning() {
        return running.get();
    }

    public boolean isComplete() {
        return complete.get();
    }
}
