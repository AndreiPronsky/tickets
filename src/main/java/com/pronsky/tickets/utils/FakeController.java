package com.pronsky.tickets.utils;

import com.pronsky.tickets.service.FlightService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;

@Component
@Slf4j
public class FakeController {

    private final FlightService service;

    public FakeController(@Lazy FlightService service) {
        this.service = service;
    }

    public void callServiceMethods() {
        Map<String, Duration> durations = service.getMinDurationForEveryCarrier();
        System.out.println(service.getDifferenceBtwMedianAndAverage());
        for (String key : durations.keySet()) {
            System.out.println("Minimum time for " + key + " is " + durations.get(key));
        }
    }
}
