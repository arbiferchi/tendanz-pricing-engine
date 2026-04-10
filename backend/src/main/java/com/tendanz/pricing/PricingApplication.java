package com.tendanz.pricing;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Main Spring Boot application class for the Pricing Engine.
 * Entry point for the application.
 */
@SpringBootApplication
public class PricingApplication {

    public static void main(String[] args) {
        SpringApplication.run(PricingApplication.class, args);
    }

    /**
     * Provide ObjectMapper bean for JSON serialization/deserialization.
     * Registers JavaTimeModule to support Java 8 date/time types (LocalDateTime, LocalDate, etc).
     *
     * @return configured ObjectMapper with JSR310 support
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }
}
