package com.robinjonsson.dwquartz.triggers;

import com.robinjonsson.dwquartz.AbstractJob;
import com.robinjonsson.dwquartz.annotations.Cron;
import com.robinjonsson.dwquartz.annotations.MisfirePolicy;
import com.robinjonsson.dwquartz.utils.InitialDelayParser;
import java.util.Collections;
import java.util.Date;
import java.util.Set;
import java.util.TimeZone;
import org.apache.commons.lang3.StringUtils;
import org.quartz.CronScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

public class CronTriggerBuilder implements CustomTriggerBuilder {

    @Override
    public boolean canBuildTrigger(final AbstractJob job) {
        return job.getClass().isAnnotationPresent(Cron.class);
    }

    @Override
    public Set<? extends Trigger> buildTriggers(final AbstractJob job) {
        final Date startAt = InitialDelayParser.parseStartAt(job);

        final Cron annotation = job.getClass().getAnnotation(Cron.class);
        final String cronExpressionLiteral = annotation.value();
        final String timeZoneLiteral = annotation.timeZone();
        final MisfirePolicy misfirePolicy = annotation.misfirePolicy();

        final TimeZone timeZone = StringUtils.isNotBlank(timeZoneLiteral)
            ? TimeZone.getTimeZone(timeZoneLiteral)
            : TimeZone.getDefault();

        final CronScheduleBuilder cronSchedule = CronScheduleBuilder
            .cronSchedule(cronExpressionLiteral)
            .inTimeZone(timeZone);

        misfirePolicy.apply(cronSchedule);

        return Collections.singleton(
            TriggerBuilder
                .newTrigger()
                .withSchedule(cronSchedule)
                .startAt(startAt)
                .build()
        );
    }
}
