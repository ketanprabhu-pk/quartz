package com.gigvistas.scheduler.service;

import com.gigvistas.scheduler.model.JobDescriptor;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.quartz.JobKey.jobKey;

@Service
@Transactional
@RequiredArgsConstructor
public class QuartzServiceImpl implements QuartzService {

    private final Scheduler scheduler;

    public JobDescriptor createJob(String group, JobDescriptor descriptor) {
        descriptor.setGroup(group);
        JobDetail jobDetail = descriptor.buildJobDetail();
        Set<Trigger> triggersForJob = descriptor.buildTriggers();
        System.out.println("About to save job with key - " + jobDetail.getKey());
        try {
            scheduler.scheduleJob(jobDetail, triggersForJob, false);
            System.out.println("Job with key - "+ jobDetail.getKey()+" saved successfully");
        } catch (SchedulerException e) {
            System.out.println("Could not save job with key - " + jobDetail.getKey()+" due to error - "+ e.getLocalizedMessage());
            throw new IllegalArgumentException(e.getLocalizedMessage());
        }
        return descriptor;
    }

    public List<JobDescriptor> findAllJobs() {
        List<JobDescriptor> jobList = new ArrayList<>();
        try {
            for (String groupName : scheduler.getJobGroupNames()) {
                for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
                    String name = jobKey.getName();
                    String group = jobKey.getGroup();
                    JobDetail jobDetail = scheduler.getJobDetail(jobKey(name, group));
                    List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobDetail.getKey());
                    jobList.add(JobDescriptor.buildDescriptor(jobDetail, triggers, scheduler));
                }
            }
        } catch (SchedulerException e) {
            System.out.println("Could not find all jobs due to error - "+ e.getLocalizedMessage());
        }
        return jobList;
    }

    @Transactional(readOnly = true)
    public Optional<JobDescriptor> findJob(String group, String name) {
        try {
            JobDetail jobDetail = scheduler.getJobDetail(jobKey(name, group));
            if(Objects.nonNull(jobDetail)) {
                List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobDetail.getKey());
                return Optional.of(
                        JobDescriptor.buildDescriptor(jobDetail, triggers, scheduler));
            }
        } catch (SchedulerException e) {
            System.out.println("Could not find job with key - "+group+"."+name+" due to error - "+e.getLocalizedMessage());
        }
        System.out.println("Could not find job with key - "+ group+" . "+ name);
        return Optional.empty();
    }

    public Optional<JobDetail> updateJob(String group, String name, JobDescriptor descriptor) {
        try {
            JobDetail oldJobDetail = scheduler.getJobDetail(jobKey(name, group));
            if(Objects.nonNull(oldJobDetail)) {
                JobDataMap jobDataMap = oldJobDetail.getJobDataMap();
                for(Map.Entry<String,Object> entry : descriptor.getData().entrySet()){
                    jobDataMap.put(entry.getKey(), entry.getValue());
                }
                JobBuilder jb = oldJobDetail.getJobBuilder();
                JobDetail newJobDetail = jb.usingJobData(jobDataMap).storeDurably().build();
                scheduler.addJob(newJobDetail, true);
                System.out.println("Updated job with key - "+ newJobDetail.getKey());
                return Optional.of(newJobDetail);
            }
            System.out.println("Could not find job with key - "+group+"."+ name+" to update" );
        } catch (SchedulerException e) {
            System.out.println("Could not find job with key -  "+group+"."+ name+"  to update due to error - "+ e.getLocalizedMessage());
        }
        return Optional.empty();
    }

    public void deleteJob(String group, String name) {
        try {
            scheduler.deleteJob(jobKey(name, group));
            System.out.println("Deleted job with key - "+ group+"."+ name);
        } catch (SchedulerException e) {
            System.out.println("Could not delete job with key - "+group+"."+ name+" due to error - "+e.getLocalizedMessage());
        }
    }

    public void pauseJob(String group, String name) {
        try {
            scheduler.pauseJob(jobKey(name, group));
            System.out.println("Paused job with key - "+ group+"."+ name);
        } catch (SchedulerException e) {
            System.out.println("Could not pause job with key - "+group+"."+ name+" due to error - "+e.getLocalizedMessage());
        }
    }

    public void resumeJob(String group, String name) {
        try {
            scheduler.resumeJob(jobKey(name, group));
            System.out.println("Resumed job with key - "+group+"."+ name);
        } catch (SchedulerException e) {
            System.out.println("Could not resume job with key - "+group+"."+ name+"due to error - "+ e.getLocalizedMessage());
        }
    }
}
