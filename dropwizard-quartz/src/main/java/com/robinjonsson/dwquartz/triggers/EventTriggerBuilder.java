package com.robinjonsson.dwquartz.triggers;

import com.robinjonsson.dwquartz.AbstractJob;
import com.robinjonsson.dwquartz.annotations.OnEvent;
import java.util.Collections;
import java.util.Date;
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
        final Date startAt = InitialDelayParser.parseStartAt(job);

        final OnEvent annotation = job.getClass().getAnnotation(OnEvent.class);
        final OnEvent.Event event = annotation.value();

        if (OnEvent.Event.APPLICATION_START.equals(event)) {
            return buildTriggerNow(startAt);
        } else if(OnEvent.Event.APPLICATION_STOP.equals(event)) {
            //TODO: Implement on app stop!
        }
        return Collections.emptySet();
    }

    private Set<? extends Trigger> buildTriggerNow(final Date startAt) {
        return Collections.singleton(
            TriggerBuilder
                .newTrigger()
                .startAt(startAt)
                .build()
        );
    }
}
