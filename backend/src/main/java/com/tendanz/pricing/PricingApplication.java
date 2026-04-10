package com.tendanz.pricing;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
        JavaTimeModule module = new JavaTimeModule();
        // Remove milliseconds globally for consistent JSON output like "2026-04-07T12:00:00"
        module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        
        mapper.registerModule(module);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }
}
