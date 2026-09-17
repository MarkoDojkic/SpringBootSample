package dev.markodojkic.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

public class JacksonUtil {
    private final ObjectMapper objectMapper;

    public JacksonUtil(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String serialize(Customer customer) throws JsonProcessingException {
        return objectMapper.writeValueAsString(customer);
    }

    public Customer deserialize(String json) throws JsonProcessingException {
        return objectMapper.readValue(json, Customer.class);
    }

    public JsonNode parse(String json) throws JsonProcessingException {
        return objectMapper.readTree(json);
    }

    public String updateJson(String json) throws JsonProcessingException {
        JsonNode root = objectMapper.readTree(json);
        if (root instanceof ObjectNode objectNode) {
            objectNode.put("processed", true);
        }
        return objectMapper.writeValueAsString(root);
    }

    public List<Customer> deserializeCustomers(String json) throws JsonProcessingException {
        return objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, Customer.class));
    }

    public record Customer(Long id, String name, String email) {
    }
}