package com.robinjonsson.dwquartz;

import org.quartz.JobDataMap;

/**
 * Store job information between fires in the job data map.
 * Implement this interface when you require initial data to be present in the job data map.
 */
public interface InitialJobData {

    JobDataMap getJobData();

}
