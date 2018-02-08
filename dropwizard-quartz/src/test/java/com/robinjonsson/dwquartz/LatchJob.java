package com.robinjonsson.dwquartz;

import java.util.concurrent.CountDownLatch;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class LatchJob extends AbstractJob {

    private final CountDownLatch executionsLeft;

    public LatchJob(
        final String testName,
        final int expectedExecutions
    ) {
        super(testName);
        this.executionsLeft = new CountDownLatch(expectedExecutions);
    }

    @Override
    protected void executeJob(final JobExecutionContext context) throws JobExecutionException {
        executionsLeft.countDown();
    }

    public CountDownLatch getExecutionsLeft() {
        return executionsLeft;
    }
}
