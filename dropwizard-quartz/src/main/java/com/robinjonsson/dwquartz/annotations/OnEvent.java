package com.robinjonsson.dwquartz.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface OnEvent {

    enum Event {
        APPLICATION_START,
        APPLICATION_STOP,
    }

    /**
     * What event to fire this job on
     */
    Event value();

}
