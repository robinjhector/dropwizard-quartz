package com.robinjonsson.dwquartz.event;

import com.robinjonsson.dwquartz.AbstractJob;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

public class EventManager {

    private static final EventManager INSTANCE = new EventManager();

    private Scheduler scheduler;
    private final Map<EventType, Set<AbstractJob>> registeredConsumers = new HashMap<>();

    private EventManager() {
    }

    public void setScheduler(final Scheduler scheduler) {
        if (this.scheduler != null) {
            throw new IllegalArgumentException("Cannot override scheduler");
        }
        this.scheduler = scheduler;
    }

    public static EventManager getInstance() {
        return INSTANCE;
    }

    public void subscribe(
        final EventType eventType,
        final AbstractJob consumer
    ) {
        registeredConsumers.putIfAbsent(eventType, new HashSet<>());
        registeredConsumers.get(eventType).add(consumer);
    }

    public void trigger(
        final EventType eventType
    ) {
        if (scheduler == null) {
            throw new IllegalStateException("Cannot trigger job before scheduler has been set!");
        }
        final Set<AbstractJob> consumers = registeredConsumers.getOrDefault(eventType, new HashSet<>());
        consumers.forEach(this::triggerNow);
    }

    private void triggerNow(final AbstractJob job) {
        try {
            scheduler.triggerJob(job.getQuartzJobKey());
        } catch (final SchedulerException e) {
            throw new IllegalArgumentException(
                String.format("Failed to trigger job=%s on event", job.getQuartzJobKey()),
                e
            );
        }
    }
}
