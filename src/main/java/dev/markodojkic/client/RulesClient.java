package dev.markodojkic.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "rules-service", url = "${app.feign.rules-url:http://localhost:8080}")
public interface RulesClient {
    @GetMapping("/api/rules/evaluate")
    String evaluate(@RequestParam(name = "age") int age);
}
