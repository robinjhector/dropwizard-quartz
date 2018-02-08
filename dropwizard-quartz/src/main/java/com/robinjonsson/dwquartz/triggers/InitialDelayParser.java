package com.robinjonsson.dwquartz.triggers;

import static java.time.Instant.now;

import com.robinjonsson.dwquartz.AbstractJob;
import com.robinjonsson.dwquartz.annotations.InitialDelay;
import java.time.temporal.ChronoUnit;
import java.util.Date;

class InitialDelayParser {

    static Date parseStartAt(final AbstractJob job) {
        if (!job.getClass().isAnnotationPresent(InitialDelay.class)) {
            return Date.from(now());
        }
        final InitialDelay annotation = job.getClass().getAnnotation(InitialDelay.class);
        final int delayTime = annotation.delay();
        final ChronoUnit delayTimeUnit = annotation.timeUnit();

        return Date.from(
            now().plus(delayTime, delayTimeUnit)
        );
    }
}
