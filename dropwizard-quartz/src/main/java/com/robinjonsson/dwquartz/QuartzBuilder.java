package com.robinjonsson.dwquartz;

import com.robinjonsson.dwquartz.triggers.CronTriggerBuilder;
import com.robinjonsson.dwquartz.triggers.CustomTriggerBuilder;
import com.robinjonsson.dwquartz.triggers.EventTriggerBuilder;
import com.robinjonsson.dwquartz.triggers.FineTunedTriggerBuilder;
import com.robinjonsson.dwquartz.triggers.IntervalTriggerBuilder;
import java.util.HashSet;
import java.util.Set;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Trigger;

/**
 * Helper class to parse annotation config & class config into Quartz JobDetails & Triggers
 */
class QuartzBuilder {

    private final Set<CustomTriggerBuilder> triggerBuilders;

    public QuartzBuilder(final Set<CustomTriggerBuilder> customTriggerBuilders) {
        triggerBuilders = new HashSet<>();
        triggerBuilders.add(new FineTunedTriggerBuilder());
        triggerBuilders.add(new CronTriggerBuilder());
        triggerBuilders.add(new IntervalTriggerBuilder());
        triggerBuilders.add(new EventTriggerBuilder());

        triggerBuilders.addAll(customTriggerBuilders);
    }

    JobDetail buildJobDetail(final AbstractJob job) {
        return JobBuilder
            .newJob(job.getClass())
            .withIdentity(job.getQuartzJobKey())
            .usingJobData(job instanceof InitialJobData ? ((InitialJobData) job).getJobData() : new JobDataMap())
            .build();
    }

    Set<? extends Trigger> buildTriggers(final AbstractJob job) throws QuartzSchedulingException {
        final CustomTriggerBuilder triggerBuilder = triggerBuilders.stream()
            .filter(tb -> tb.canBuildTrigger(job))
            .findFirst()
            .orElseThrow(() -> new QuartzSchedulingException(String.format(
                "No suitable trigger builders for class %s. Can not schedule this job!",
                job.getClass()
            )));

        return triggerBuilder.buildTriggers(job);
    }
}
