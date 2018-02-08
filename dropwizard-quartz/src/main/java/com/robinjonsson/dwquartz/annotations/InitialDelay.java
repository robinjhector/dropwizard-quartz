package com.robinjonsson.dwquartz.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.temporal.ChronoUnit;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface InitialDelay {

    /**
     * A fixed delay before the first trigger of this job.
     */
    int delay();

    /**
     * The time unit to correlate with the initial fixed delay.
     */
    ChronoUnit timeUnit() default ChronoUnit.SECONDS;

}
