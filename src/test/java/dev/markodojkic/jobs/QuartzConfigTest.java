package dev.markodojkic.jobs;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Trigger;

class QuartzConfigTest {
    @Test
    void shouldScheduleExampleJobEveryThirtySeconds() {
        QuartzConfig config = new QuartzConfig();
        JobDetail detail = config.exampleJobDetail();
        Trigger trigger = config.exampleTrigger(detail);

        assertEquals(ExampleQuartzJob.class, detail.getJobClass());
        assertEquals("exampleQuartzTrigger", trigger.getKey().getName());
        assertEquals("0/30 * * * * ?", ((CronTrigger) trigger).getCronExpression());
    }
}
