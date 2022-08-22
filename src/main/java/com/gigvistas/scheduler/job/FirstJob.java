package com.gigvistas.scheduler.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

@Component
public class FirstJob implements Job {

    @Override
    public void execute(JobExecutionContext context){
        System.out.println("Hello World This is first quarts Scheduler");

    }
}
