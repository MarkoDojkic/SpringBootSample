package dev.markodojkic.jackson;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import java.util.List;

public class JacksonUtil {
    private final ObjectMapper objectMapper;

    public JacksonUtil(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String serialize(Customer customer) {
        return objectMapper.writeValueAsString(customer);
    }

    public Customer deserialize(String json) {
        return objectMapper.readValue(json, Customer.class);
    }

    public JsonNode parse(String json) {
        return objectMapper.readTree(json);
    }

    public String updateJson(String json) {
        JsonNode root = objectMapper.readTree(json);
        if (root instanceof ObjectNode objectNode) {
            objectNode.put("processed", true);
        }
        return objectMapper.writeValueAsString(root);
    }

    public List<Customer> deserializeCustomers(String json) {
        return objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, Customer.class));
    }

    public record Customer(Long id, String name, String email) {
    }
}