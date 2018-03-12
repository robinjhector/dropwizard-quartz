package com.robinjonsson.dwquartz.triggers;

import static java.util.stream.Collectors.toSet;

import com.robinjonsson.dwquartz.AbstractJob;
import com.robinjonsson.dwquartz.annotations.Interval;
import com.robinjonsson.dwquartz.annotations.MisfirePolicy;
import com.robinjonsson.dwquartz.utils.InitialDelayParser;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Set;
import org.quartz.DailyTimeIntervalScheduleBuilder;
import org.quartz.DateBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

public class IntervalTriggerBuilder implements CustomTriggerBuilder {

    @Override
    public boolean canBuildTrigger(final AbstractJob job) {
        return job.getClass().isAnnotationPresent(Interval.class);
    }

    @Override
    public Set<? extends Trigger> buildTriggers(final AbstractJob job) {
        final Date startAt = InitialDelayParser.parseStartAt(job);

        final Interval annotation = job.getClass().getAnnotation(Interval.class);
        final DayOfWeek[] dayOfWeekEnums = annotation.daysOfWeek();
        final int frequency = annotation.frequency();
        final ChronoUnit frequencyTimeUnit = annotation.timeUnit();
        final MisfirePolicy misfirePolicy = annotation.misfirePolicy();

        final long intervalSeconds = Duration.of(frequency, frequencyTimeUnit).getSeconds();
        final Set<Integer> dayOfWeek = Arrays.stream(dayOfWeekEnums).map(DayOfWeek::getValue).collect(toSet());

        final DailyTimeIntervalScheduleBuilder schedule = DailyTimeIntervalScheduleBuilder
            .dailyTimeIntervalSchedule()
            .withInterval((int) intervalSeconds, DateBuilder.IntervalUnit.SECOND)
            .onDaysOfTheWeek(dayOfWeek);

        misfirePolicy.apply(schedule);

        return Collections.singleton(
            TriggerBuilder
                .newTrigger()
                .withSchedule(schedule)
                .startAt(startAt)
                .build()
        );
    }
}
