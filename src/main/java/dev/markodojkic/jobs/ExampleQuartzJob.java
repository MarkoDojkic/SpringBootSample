package dev.markodojkic.jobs;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import dev.markodojkic.jackson.JacksonUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import tools.jackson.core.JacksonException;
import java.util.List;

public class ExampleQuartzJob implements Job {

    private final JacksonUtil jackson;

    public ExampleQuartzJob(ObjectMapper objectMapper) {
        this.jackson = new JacksonUtil(objectMapper);
    }

    @Override
    public void execute(JobExecutionContext context) {
        System.out.println("Quartz job executed: " + context.getFireTime());

        JacksonUtil.Customer customer =
                new JacksonUtil.Customer(
                        1L,
                        "Marko",
                        "marko@example.com"
                );

        List<JacksonUtil.Customer> customers =
                List.of(
                        customer,
                        new JacksonUtil.Customer(
                                2L,
                                "Test User",
                                "test@example.com"
                        )
                );

        try {
            // serialize()
            String customerJson = jackson.serialize(customer);
            System.out.println("Serialized customer: " + customerJson);

            // deserialize()
            JacksonUtil.Customer deserialized =
                    jackson.deserialize(customerJson);
            System.out.println("Deserialized customer: " + deserialized);

            // parse()
            String json = """
                    {
                        "id": 1,
                        "name": "Marko",
                        "email": "marko@example.com"
                    }
                    """;

            System.out.println("Parsed JSON: " + jackson.parse(json));

            // updateJson()
            String updatedJson = jackson.updateJson(json);
            System.out.println("Updated JSON: " + updatedJson);

            // deserializeCustomers()
            ObjectMapper objectMapper = new JsonMapper();
            String customersJson = objectMapper.writeValueAsString(customers);

            List<JacksonUtil.Customer> deserializedCustomers =
                    jackson.deserializeCustomers(customersJson);

            System.out.println("Deserialized customers: " + deserializedCustomers);

            System.out.println("QUARTZ_SMOKE_TEST: Jackson migration example executed");
        } catch (JacksonException e) {
            throw new IllegalStateException("Jackson processing failed", e);
        }
    }
}