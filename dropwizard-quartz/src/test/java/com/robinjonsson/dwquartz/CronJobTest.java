package com.robinjonsson.dwquartz;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.robinjonsson.dwquartz.annotations.Cron;
import com.robinjonsson.dwquartz.annotations.InitialDelay;
import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.After;
import org.junit.Test;

public class CronJobTest {

    private QuartzManager scheduler;

    private final QuartzConfiguration testConfig = new QuartzConfiguration() {

    };

    @After
    public void tearDown() throws Exception {
        scheduler.stop();
    }

    @Test
    public void shouldExecuteCronJob() throws Exception {
        final CronJob cronJob = new CronJob(1);
        scheduler = createScheduler(cronJob);
        scheduler.start();

        final CountDownLatch executionsLeft = cronJob.getExecutionsLeft();
        assertTrue(executionsLeft.await(1, TimeUnit.SECONDS));
    }

    @Test
    public void shouldExecuteEachSecond() throws Exception {
        final CronJob cronJob = new CronJob(3);
        scheduler = createScheduler(cronJob);
        scheduler.start();

        final CountDownLatch executionsLeft = cronJob.getExecutionsLeft();
        assertTrue(executionsLeft.await(3, TimeUnit.SECONDS));
    }

    @Test
    public void shouldExecuteAfterInitialDelay() throws Exception {
        final CronJobInitialDelay cronJob = new CronJobInitialDelay();
        scheduler = createScheduler(cronJob);
        scheduler.start();

        final CountDownLatch executionsLeft = cronJob.getExecutionsLeft();
        assertFalse(executionsLeft.await(1, TimeUnit.SECONDS));
        assertTrue(executionsLeft.await(1, TimeUnit.SECONDS));
    }

    private QuartzManager createScheduler(final AbstractJob... jobs) {
        return new QuartzManager(
            testConfig,
            new HashSet<>(Arrays.asList(jobs))
        );
    }

    @Cron("0/1 * * * * ?")
    public static class CronJob extends LatchJob {
        public CronJob(final int executions) {
            super("CronJob", executions);
        }
    }

    @Cron("0/1 * * * * ?")
    @InitialDelay(delay = 2)
    public static class CronJobInitialDelay extends LatchJob {
        public CronJobInitialDelay() {
            super("CronJobInitialDelay", 1);
        }
    }
}
