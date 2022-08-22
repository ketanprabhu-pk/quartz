package com.gigvistas.scheduler.service;

import com.gigvistas.scheduler.DTO.QuartzTimer;
import com.gigvistas.scheduler.job.FirstJob;
import com.gigvistas.scheduler.utils.TimerUtil;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Service
public class SchedulerServiceFirstJob {

    @Autowired
    private Scheduler scheduler;

    public void schedule(final Class helloClass, final QuartzTimer helloTimer){
        final JobDetail jobDetail= TimerUtil.buildJobDetails(helloClass,helloTimer);
        final Trigger trigger=TimerUtil.buildTrigger(helloClass,helloTimer);

        try {
            scheduler.scheduleJob(jobDetail,trigger);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @PostConstruct
    public void init(){
        try {
            scheduler.start();
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    public void hello(){
        final QuartzTimer helloTimer= new QuartzTimer();
        helloTimer.setTotalFireCount(5);
        helloTimer.setRemainingFireCount(helloTimer.getTotalFireCount());
        helloTimer.setRepeatIntervalMs(5000);
        helloTimer.setInitialOffsetMs(1000);
        helloTimer.setCallbackData("My callback data");
        schedule(FirstJob.class, helloTimer);
        System.out.println("Hello Word");
    }

    @PreDestroy
    public void preDestroy(){
        try {
            scheduler.shutdown();
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
        JobDetail j= JobBuilder.newJob(FirstJob.class).withIdentity("","").build();
    }


}
