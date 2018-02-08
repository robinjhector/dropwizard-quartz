package com.robinjonsson.dwquartz;

import com.robinjonsson.dwquartz.triggers.CustomTriggerBuilder;
import io.dropwizard.lifecycle.Managed;
import java.util.Collections;
import java.util.Properties;
import java.util.Set;
import java.util.function.Supplier;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuartzScheduler implements Managed {

    private static final Logger LOG = LoggerFactory.getLogger(QuartzScheduler.class);

    private final QuartzConfiguration config;
    private final Set<AbstractJob> jobs;
    private final QuartzBuilder quartzBuilder;

    private Scheduler scheduler;

    public QuartzScheduler(
        final QuartzConfiguration config,
        final Set<AbstractJob> jobs
    ) {
        this(
            config,
            jobs,
            Collections.emptySet()
        );
    }

    public QuartzScheduler(
        final QuartzConfiguration config,
        final Set<AbstractJob> jobs,
        final Set<CustomTriggerBuilder> customCustomTriggerBuilders
    ) {
        this.config = config;
        this.jobs = jobs;
        this.quartzBuilder = new QuartzBuilder(customCustomTriggerBuilders);
    }

    @Override
    public void start() throws Exception {
        LOG.info("Starting Quartz Scheduler");
        final StdSchedulerFactory schedulerFactory = new StdSchedulerFactory();
        final InstantiatedJobFactory jobFactory = new InstantiatedJobFactory(jobs);

        if (!config.getQuartzConfiguration().isEmpty()) {
            final Properties schedulerProps = new Properties();
            schedulerProps.putAll(config.getQuartzConfiguration());
            schedulerFactory.initialize(schedulerProps);
        }

        scheduler = schedulerFactory.getScheduler();
        scheduler.setJobFactory(jobFactory);
        scheduler.start();

        jobs.forEach(this::scheduleJob);
    }

    private void scheduleJob(final AbstractJob job) {
        final JobDetail jobDetail = quartzBuilder.buildJobDetail(job);
        final Set<? extends Trigger> triggers = quartzBuilder.buildTriggers(job);

        try {
            scheduler.scheduleJob(jobDetail, triggers, true);
            LOG.info("Scheduled {} with trigger {}", jobDetail);
        } catch (final SchedulerException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void stop() throws Exception {
        LOG.info("Stopping Quartz Scheduler... (Waiting for all jobs to complete)");
        scheduler.shutdown(true);
    }

    /**
     * Get's the quartz scheduler. Supplier method, because the scheduler is instantiated on start()
     * @return A supplier pointing to the scheduler
     */
    public Supplier<Scheduler> getScheduler() {
        return () -> scheduler;
    }
}
