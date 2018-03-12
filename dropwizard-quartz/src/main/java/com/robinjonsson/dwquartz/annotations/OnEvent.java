package com.robinjonsson.dwquartz.annotations;

import com.robinjonsson.dwquartz.event.EventType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface OnEvent {

    /**
     * What event to fire this job on
     */
    EventType value();

}
