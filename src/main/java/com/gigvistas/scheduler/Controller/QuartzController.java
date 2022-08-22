package com.gigvistas.scheduler.Controller;

import com.gigvistas.scheduler.model.JobDescriptor;
import com.gigvistas.scheduler.service.QuartzService;
import lombok.RequiredArgsConstructor;

import org.quartz.JobDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/quartz")
@RequiredArgsConstructor
public class QuartzController {

    private final QuartzService quartzService;

    @RequestMapping(value = "/groups/jobs", method = RequestMethod.GET)
    public ResponseEntity<JobDescriptor> findAllJobs() {
        List<JobDescriptor> jobs = quartzService.findAllJobs();
        if (jobs.isEmpty()) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity(jobs, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/groups/{group}/jobs", method = RequestMethod.POST)
    public ResponseEntity<JobDescriptor> createJob(@PathVariable String group, @RequestBody JobDescriptor descriptor) {
        return new ResponseEntity(quartzService.createJob(group, descriptor), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/groups/{group}/jobs/{name}", method = RequestMethod.GET)
    public ResponseEntity<JobDescriptor> findJob(@PathVariable String group, @PathVariable String name) {
        return quartzService.findJob(group, name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @RequestMapping(value = "/groups/{group}/jobs/{name}", method = RequestMethod.PUT)
    public ResponseEntity<JobDetail> updateJob(@PathVariable String group, @PathVariable String name, @RequestBody JobDescriptor descriptor) {
        return quartzService.updateJob(group, name, descriptor).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @RequestMapping(value = "/groups/{group}/jobs/{name}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteJob(@PathVariable String group, @PathVariable String name) {
        quartzService.deleteJob(group, name);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/groups/{group}/jobs/{name}/pause", method = RequestMethod.PATCH)
    public ResponseEntity<Void> pauseJob(@PathVariable String group, @PathVariable String name) {
        quartzService.pauseJob(group, name);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/groups/{group}/jobs/{name}/resume", method = RequestMethod.PATCH)
    public ResponseEntity<Void> resumeJob(@PathVariable String group, @PathVariable String name) {
        quartzService.resumeJob(group, name);
        return ResponseEntity.noContent().build();
    }
}
