package com.robinjonsson.dwquartz;

import java.util.Set;
import org.quartz.Trigger;

public interface FineTunedTriggers {

    Set<? extends Trigger> getTriggers();

}
