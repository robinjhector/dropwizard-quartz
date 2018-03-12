package com.robinjonsson.dwquartz;

import com.robinjonsson.dwquartz.event.EventManager;
import com.robinjonsson.dwquartz.event.EventType;
import com.robinjonsson.dwquartz.triggers.CustomTriggerBuilder;
import io.dropwizard.lifecycle.Managed;
import java.util.Collections;
import java.util.Optional;
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

public class QuartzManager implements Managed {

    private static final Logger LOG = LoggerFactory.getLogger(QuartzManager.class);

    private final QuartzConfiguration config;
    private final Set<AbstractJob> jobs;
    private final QuartzBuilder quartzBuilder;

    private Scheduler scheduler;

    public QuartzManager(
        final QuartzConfiguration config,
        final Set<AbstractJob> jobs
    ) {
        this(
            config,
            jobs,
            Collections.emptySet()
        );
    }

    public QuartzManager(
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

        EventManager.getInstance().setScheduler(scheduler);
        EventManager.getInstance().trigger(EventType.APPLICATION_START);
    }

    private void scheduleJob(final AbstractJob job) {
        final JobDetail jobDetail = quartzBuilder.buildJobDetail(job);

        try {
            final Set<? extends Trigger> triggers = quartzBuilder.buildTriggers(job);
            final Optional<? extends Trigger> firstTrigger = triggers.stream().findFirst();
            scheduler.scheduleJob(jobDetail, triggers, true);

            LOG.info("Scheduled [group={}, name={}] with trigger [class={}, nextExecution={}]",
                jobDetail.getKey().getGroup(),
                jobDetail.getKey().getName(),
                firstTrigger.get().getClass().getSimpleName(),
                firstTrigger.get().getNextFireTime()
            );
        } catch (final QuartzSchedulingException | SchedulerException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void stop() throws Exception {
        LOG.info("Stopping Quartz Scheduler... (Waiting for all jobs to complete)");
        EventManager.getInstance().trigger(EventType.APPLICATION_STOP);
        //Wait for event type APPLICATION_STOP to trigger jobs
        Thread.sleep(100);
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
