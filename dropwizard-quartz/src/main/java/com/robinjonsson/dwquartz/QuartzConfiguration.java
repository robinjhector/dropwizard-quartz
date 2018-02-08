package com.robinjonsson.dwquartz;

import java.util.Collections;
import java.util.Map;

public interface QuartzConfiguration {

    default Map<String, String> getQuartzConfiguration() {
        return Collections.emptyMap();
    }
}
