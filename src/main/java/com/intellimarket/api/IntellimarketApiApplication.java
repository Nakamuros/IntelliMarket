package com.intellimarket.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;

@SpringBootApplication(exclude = { JacksonAutoConfiguration.class })
public class IntellimarketApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(IntellimarketApiApplication.class, args);
    }
}
