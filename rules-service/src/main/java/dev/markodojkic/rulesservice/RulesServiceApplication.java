package dev.markodojkic.ruleservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class RulesServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(RulesServiceApplication.class, args);
    }
}
