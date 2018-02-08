package com.robinjonsson.dwquartz.triggers;

import com.robinjonsson.dwquartz.AbstractJob;
import java.util.Set;
import org.quartz.Trigger;

public interface CustomTriggerBuilder {

    boolean canBuildTrigger(final AbstractJob job);

    Set<? extends Trigger> buildTriggers(final AbstractJob job);

}
