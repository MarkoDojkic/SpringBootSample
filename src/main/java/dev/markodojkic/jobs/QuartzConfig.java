package dev.markodojkic.jobs;

import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {
    @Bean
    JobDetail exampleJobDetail() {
        return JobBuilder.newJob(ExampleQuartzJob.class)
                .withIdentity("exampleQuartzJob")
                .storeDurably()
                .build();
    }

    @Bean
    Trigger exampleTrigger(JobDetail exampleJobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(exampleJobDetail)
                .withIdentity("exampleQuartzTrigger")
                .withSchedule(CronScheduleBuilder.cronSchedule("0/30 * * * * ?"))
                .build();
    }
}
