package com.robinjonsson.dwquartz.triggers;

import com.robinjonsson.dwquartz.AbstractJob;
import com.robinjonsson.dwquartz.annotations.OnEvent;
import com.robinjonsson.dwquartz.event.EventManager;
import com.robinjonsson.dwquartz.event.EventType;
import java.sql.Date;
import java.time.Instant;
import java.util.Collections;
import java.util.Set;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

public class EventTriggerBuilder implements CustomTriggerBuilder {

    @Override
    public boolean canBuildTrigger(final AbstractJob job) {
        return job.getClass().isAnnotationPresent(OnEvent.class);
    }

    @Override
    public Set<? extends Trigger> buildTriggers(final AbstractJob job) {
        final OnEvent annotation = job.getClass().getAnnotation(OnEvent.class);
        final EventType eventType = annotation.value();

        //Seems like this is the only way to make a trigger with no execution schedule.
        final Instant year4000 = Instant.parse("4000-01-01T00:00:00.000Z");

        EventManager.getInstance().subscribe(eventType, job);

        return Collections.singleton(
            TriggerBuilder
                .newTrigger()
                .startAt(Date.from(year4000))
                .build()
        );
    }
}
