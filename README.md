# Quartz integration for Dropwizard

[![Build Status](https://travis-ci.org/izrobin/dropwizard-quartz.svg?branch=master)](https://travis-ci.org/izrobin/dropwizard-quartz)

### About
This project aims to ease the integration of quartz to Dropwizard, by making configuration easy with annotations and simple configuration.

### Get started

Modify your existing Dropwizard configuration class, to implement the interface `QuartzConfiguration`.
If you want, you can implement the method `getQuartzConfiguration`, to specify quartz-specific configuration, in key-value pairs.
See the [Quartz Documentation](http://www.quartz-scheduler.org/documentation/quartz-2.x/configuration/) for allowed configuration values.
Otherwise a default config will be used.

Create some job classes. Each class needs to inherit from `AbstractJob`.
The constructor of this class takes 3 optional parameters:

| Parameter         | Default                                                 | Description                                                                                     |
| ----------------- | ------------------------------------------------------- | ------------------------------------------------------------------------------------------------|
| jobName           | getClass().getCanonicalName()                           | The name for your job, as registered by the Quartz scheduler                                    |
| jobGroup          | "DEFAULT"                                               | The group, that your job belongs to. The combination of jobName and jobGroup needs to be unique |
| metricsRegistry   | SharedMetricRegistries.getOrCreate("dropwizard-quartz") | A custom metrics registry to measure timing etc.                                                |

To configure scheduling for your job, see [Scheduling](#scheduling)

Instantiate all of your job classes, with their needed dependencies, 
and then manage the `QuartzManager` with the lifecycle.

Example of an application class:

```java
public class MyApplication extends Application<MyConfiguration> {

    public static void main(final String[] args) throws Exception {
        new MyApplication().run(args);
    }

    @Override
    public void run(final MyConfiguration config, final Environment environment) throws Exception {
        final Set<AbstractJob> jobs = new HashSet<>(Arrays.asList(
            new MyJobClass(),
            new AnotherJobClass()
        ));
        final QuartzManager quartzManager = new QuartzManager(
            config,
            jobs
        );
        environment.lifecycle().manage(quartzManager);
    }
}
```


### Scheduling

There are multiple ways of scheduling your jobs with dropwizard-quartz. The simplest way is to use annotations:

#### Interval
```java
@Interval(frequency = 2, timeUnit = ChronoUnit.MINUTES)
public class MyJob extends AbstractJob {
    @Override
    public void executeJob(final JobExecutionContext ctx) {
        //Your job code
    }
}
```
Above job will be run every 2 minutes, indefinitely 

#### Cron
```java
@Cron(value = "0 0 2 ? DEC *", timeZone = "UTC")
public class MyJob extends AbstractJob {
    @Override
    public void executeJob(final JobExecutionContext ctx) {
        //Your job code
    }
}
```
Above job will be run at 2am every day in december, at UTC time.


#### On Event
```java
@OnEvent(EventType.APPLICATION_START)
public class MyJob extends AbstractJob {
    @Override
    public void executeJob(final JobExecutionContext ctx) {
        //Your job code
    }
}
```
Above job will be run as soon as Quartz is configured and running. 
There are 2 types of events built-in:
 - `EventType.APPLICATION_START`
 - `EventType.APPLICATION_STOP`
 
More events might be added in the future.

#### Initial Delay
```java
@InitialDelay(delay = 5, timeUnit = ChronoUnit.SECONDS)
@Interval(frequency = 5, timeUnit = ChronoUnit.MINUTES)
public class MyJob extends AbstractJob {
    @Override
    public void executeJob(final JobExecutionContext ctx) {
        //Your job code
    }
}
```

All types of scheduling supports the use of an initial delay. 
The above class will be executed 5 minutes after `APPLICATION_START`, 
and executed every 5 minutes after that, indefinitely  


#### Custom annotations

You can easily extend this library by creating your own custom annotations for scheduling.
To do so, create a class implementing `CustomTriggerBuilder`. Implement the two methods:

```java
boolean canBuildTrigger(final AbstractJob job);
```
A qualifying method to determine if your TriggerBuilder should built the trigger for the supplied `AbstractJob`.

```java
Set<? extends Trigger> buildTriggers(final AbstractJob job);
```
Return a set of triggers to be scheduled for this job.

And later add it to the `QuartzManager`

````java
final Set<CustomTriggerBuilder> customTriggerBuilders = ...
final QuartzManager quartzManager = new QuartzManager(
            config,
            jobs,
            customTriggerBuilders
        );
````


##### Custom triggers via code

If the above seems like too much work, you can also let your job class itself, declare the triggers.
Implement the interface `FineTunedTriggers` directly from your class extending `AbstractJob`.
And return your desired triggers in `Set<? extends Trigger> getTriggers();`
