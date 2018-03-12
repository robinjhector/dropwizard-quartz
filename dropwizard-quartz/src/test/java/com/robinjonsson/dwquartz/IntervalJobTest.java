package com.robinjonsson.dwquartz;

import static org.junit.Assert.assertTrue;

import com.robinjonsson.dwquartz.annotations.Interval;
import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;
import org.junit.After;
import org.junit.Test;

public class IntervalJobTest {

    private QuartzManager scheduler;

    private final QuartzConfiguration testConfig = new QuartzConfiguration() {

    };

    @After
    public void tearDown() throws Exception {
        scheduler.stop();
    }

    @Test
    public void name() throws Exception {
        final Every1Seconds job = new Every1Seconds(2);

        scheduler = createScheduler(job);
        scheduler.start();

        assertTrue(job.getExecutionsLeft().await(2, TimeUnit.SECONDS));
    }

    @Interval(frequency = 1)
    public static class Every1Seconds extends LatchJob {

        public Every1Seconds(final int expectedExecutions) {
            super("IntervalJob", expectedExecutions);
        }
    }

    private QuartzManager createScheduler(final AbstractJob... jobs) {
        return new QuartzManager(
            testConfig,
            new HashSet<>(Arrays.asList(jobs))
        );
    }
}
