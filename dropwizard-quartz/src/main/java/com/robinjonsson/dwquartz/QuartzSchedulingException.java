package com.robinjonsson.dwquartz;

public class QuartzSchedulingException extends Exception {

    public QuartzSchedulingException(final String message) {
        super(message);
    }

    public QuartzSchedulingException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
