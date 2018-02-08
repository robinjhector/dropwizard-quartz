package com.robinjonsson.dwquartz.triggers;

import com.robinjonsson.dwquartz.AbstractJob;
import com.robinjonsson.dwquartz.FineTunedTriggers;
import java.util.Set;
import org.quartz.Trigger;

public class FineTunedTriggerBuilder implements CustomTriggerBuilder {
    @Override
    public boolean canBuildTrigger(final AbstractJob job) {
        return job instanceof FineTunedTriggers;
    }

    @Override
    public Set<? extends Trigger> buildTriggers(final AbstractJob job) {
        return ((FineTunedTriggers) job).getTriggers();
    }
}
