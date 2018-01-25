package com.robinjonsson.dwquartz;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import com.codahale.metrics.Timer;
import org.apache.commons.lang3.StringUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;

public abstract class AbstractJob implements Job {

    public static final String METRICS_NAME = "dropwizard-quartz";

    private final Timer timer;
    private final JobKey quartzJobKey;

    public AbstractJob() {
        this(null, null, SharedMetricRegistries.getOrCreate(METRICS_NAME));
    }

    public AbstractJob(final MetricRegistry metricRegistry) {
        this(null, null, metricRegistry);
    }

    public AbstractJob(final String jobName, final MetricRegistry metricRegistry) {
        this(jobName, null, metricRegistry);
    }

    public AbstractJob(
        final String jobName,
        final String groupName,
        final MetricRegistry registry
    ) {
        final String name = StringUtils.isNotBlank(jobName) ? jobName : getClass().getName();
        this.quartzJobKey = JobKey.jobKey(name, groupName);

        this.timer = registry.timer(quartzJobKey.toString());
    }

    @Override
    public void execute(final JobExecutionContext context) throws JobExecutionException {
        try (Timer.Context ignored = timer.time()) {
            executeJob(context);
        }
    }

    public abstract void executeJob(final JobExecutionContext context) throws JobExecutionException;

    public JobKey getQuartzJobKey() {
        return quartzJobKey;
    }
}
