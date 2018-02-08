package com.robinjonsson.dwquartz;

import java.util.Set;
import org.quartz.Job;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;

class InstantiatedJobFactory implements JobFactory {

    private final Set<AbstractJob> jobs;

    InstantiatedJobFactory(final Set<AbstractJob> jobs) {
        this.jobs = jobs;
    }

    @Override
    public Job newJob(
        final TriggerFiredBundle bundle,
        final Scheduler scheduler
    ) throws SchedulerException {
        final Class<? extends Job> jobClass = bundle.getJobDetail().getJobClass();
        final JobKey requestedJobKey = bundle.getJobDetail().getKey();
        for (final AbstractJob job : jobs) {
            if (job.getQuartzJobKey().equals(requestedJobKey) && job.getClass().equals(jobClass)) {
                return job;
            }
        }
        throw new IllegalArgumentException(
            String.format("No job found for requested class %s with key %s", jobClass.getName(), requestedJobKey)
        );
    }
}
