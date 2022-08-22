package com.gigvistas.scheduler.utils;

import com.gigvistas.scheduler.DTO.QuartzTimer;
import org.quartz.*;

import java.util.Date;

public class TimerUtil {
    public static JobDetail buildJobDetails(final Class helloClass, final QuartzTimer helloTimer){
        final JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(helloClass.getSimpleName(), helloTimer);
        return JobBuilder.newJob(helloClass)
                .withIdentity(helloClass.getSimpleName())
                .setJobData(jobDataMap).build();
    }

    public static Trigger buildTrigger(final Class helloClass, final  QuartzTimer helloTimer) {
        SimpleScheduleBuilder builder = SimpleScheduleBuilder.simpleSchedule().withIntervalInMilliseconds(helloTimer.getRepeatIntervalMs());

        if (helloTimer.isRunForever()) {
            builder = builder.repeatForever();
        } else {
            builder = builder.withRepeatCount(helloTimer.getTotalFireCount() - 1);
        }

        return TriggerBuilder
                .newTrigger()
                .withIdentity(helloClass.getSimpleName())
                .withSchedule(builder)
                .startAt(new Date(System.currentTimeMillis() + helloTimer.getInitialOffsetMs()))
                .build();
    }
}

