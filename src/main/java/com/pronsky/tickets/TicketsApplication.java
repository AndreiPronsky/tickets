package com.pronsky.tickets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pronsky.tickets.controller.impl.FakeController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;

@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
public class TicketsApplication {

    private final FakeController controller;

    public static void main(String[] args) {
        SpringApplication.run(TicketsApplication.class, args);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @EventListener
    public void callServiceMethods(WebServerInitializedEvent event) {
        controller.callServiceMethods();
    }
}
