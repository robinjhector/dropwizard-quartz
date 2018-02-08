package com.robinjonsson.dwquartz.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Cron {

    /**
     * A quartz CRON expression
     */
    String value();

    /**
     * The timezone for the cron expression
     */
    String timeZone() default "";


    MisfirePolicy misfirePolicy() default MisfirePolicy.IGNORE;
}
