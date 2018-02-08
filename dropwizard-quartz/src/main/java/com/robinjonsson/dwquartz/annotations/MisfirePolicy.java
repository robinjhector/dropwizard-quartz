package com.robinjonsson.dwquartz.annotations;

import org.quartz.CronScheduleBuilder;
import org.quartz.DailyTimeIntervalScheduleBuilder;

/**
 * Instructions to the scheduler on what to do upon misfires (Exceptions in a job for instance)
 */
public enum MisfirePolicy {
    /**
     * Will not try and re-fire the job.
     */
    IGNORE(-1),
    /**
     * Will retry instantly.
     */
    RETRY_NOW(1);
    private final int policyId;

    MisfirePolicy(final int quartzPolicyId) {
        this.policyId = quartzPolicyId;
    }

    public int getPolicyId() {
        return policyId;
    }

    /**
     * Applies misfire policy to a cron schedule
     * @param scheduleBuilder
     */
    public void apply(final CronScheduleBuilder scheduleBuilder) {
        if (this == IGNORE) {
            scheduleBuilder.withMisfireHandlingInstructionIgnoreMisfires();
        } else if (this == RETRY_NOW) {
            scheduleBuilder.withMisfireHandlingInstructionFireAndProceed();
        } else {
            scheduleBuilder.withMisfireHandlingInstructionDoNothing();
        }
    }

    /**
     * Applies misfire policy to a daily time interval schedule
     * @param schedule
     */
    public void apply(final DailyTimeIntervalScheduleBuilder schedule) {
        if (this == IGNORE) {
            schedule.withMisfireHandlingInstructionIgnoreMisfires();
        } else if (this == RETRY_NOW) {
            schedule.withMisfireHandlingInstructionFireAndProceed();
        } else {
            schedule.withMisfireHandlingInstructionDoNothing();
        }
    }
}
