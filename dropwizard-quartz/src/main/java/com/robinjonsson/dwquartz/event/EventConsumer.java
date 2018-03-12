package com.robinjonsson.dwquartz.event;

public interface EventConsumer {

    void onEvent(EventType eventType, String eventMessage);

}
