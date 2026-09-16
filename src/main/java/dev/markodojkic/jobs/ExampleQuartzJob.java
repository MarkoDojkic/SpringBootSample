package dev.markodojkic.jobs;

import org.quartz.Job;
import org.quartz.JobExecutionContext;

public class ExampleQuartzJob implements Job {
    @Override
    public void execute(JobExecutionContext context) {
        System.out.println("Quartz job executed: " + context.getFireTime());
    }
}
