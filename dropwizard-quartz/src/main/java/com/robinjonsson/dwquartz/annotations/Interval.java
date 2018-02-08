package com.robinjonsson.dwquartz.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.DayOfWeek;
import java.time.temporal.ChronoUnit;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Interval {

    /**
     * Frequency when to fire this job. Number of seconds,minutes, etc.
     */
    int frequency();

    /**
     * TimeUnit to specify the interval to fire.
     */
    ChronoUnit timeUnit() default ChronoUnit.SECONDS;

    /**
     * Optionally fire this only on the specified days
     */
    DayOfWeek[] daysOfWeek() default {
        DayOfWeek.MONDAY,
        DayOfWeek.TUESDAY,
        DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY,
        DayOfWeek.FRIDAY,
        DayOfWeek.SATURDAY,
        DayOfWeek.SUNDAY
    };

    /**
     * How to handle exceptions & misfires
     */
    MisfirePolicy misfirePolicy() default MisfirePolicy.IGNORE;

}
