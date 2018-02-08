package com.robinjonsson.dwquartz;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.robinjonsson.dwquartz.annotations.Cron;
import com.robinjonsson.dwquartz.annotations.InitialDelay;
import java.util.Arrays;
import java.util.HashSet;
import org.junit.After;
import org.junit.Test;

public class CronJobTest {

    private QuartzScheduler scheduler;

    private final QuartzConfiguration testConfig = new QuartzConfiguration() {

    };

    @After
    public void tearDown() throws Exception {
        scheduler.stop();
    }

    @Test
    public void shouldExecuteCronJob() throws Exception {
        final CronJob cronJob = new CronJob();
        scheduler = createScheduler(cronJob);
        scheduler.start();

        Thread.sleep(100);

        assertFalse(cronJob.isComplete());
        assertTrue(cronJob.isRunning());

        cronJob.getLatch().countDown();

        assertTrue(cronJob.isComplete());
        assertFalse(cronJob.isRunning());
    }

    private QuartzScheduler createScheduler(final AbstractJob... jobs) {
        return new QuartzScheduler(
            testConfig,
            new HashSet<>(Arrays.asList(jobs))
        );
    }

    @Cron("0/1 * * * * ?")
    public static class CronJob extends LatchJob {
        public CronJob() {
            super("CronJob");
        }
    }

    @Cron("0/1 * * * * ?")
    @InitialDelay(delay = 5)
    public static class CronJobInitialDelay extends LatchJob {
        public CronJobInitialDelay() {
            super("CronJobInitialDelay");
        }
    }
}
