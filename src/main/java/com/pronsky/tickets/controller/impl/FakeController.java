package com.pronsky.tickets.controller.impl;

import com.pronsky.tickets.controller.Controller;
import com.pronsky.tickets.service.FlightService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;

@Component
@Slf4j
public class FakeController implements Controller {

    private final FlightService service;

    public FakeController(@Lazy FlightService service) {
        this.service = service;
    }

    @Override
    public void callServiceMethods() {
        service.saveAll(service.deserialize());
        double difference = service.getDifferenceBtwMedianAndAverage();
        if (difference < 0) {
            System.out.println("\nMedian price is higher than average for " + Math.abs(difference) + "\n");
        } else if (difference > 0) {
            System.out.println("\nMedian price is lower than average for " + Math.abs(difference) + "\n");
        } else {
            System.out.println("\nMedian price is equal\n");
        }

        Map<String, Duration> durations = service.getMinDurationForEveryCarrier();
        for (String key : durations.keySet()) {
            Duration duration = durations.get(key);
            int hours = duration.toHoursPart();
            int minutes = duration.toMinutesPart();
            System.out.println("Minimum time for " + key + " is " + hours + " Hours and " + minutes + " Minutes");
        }
    }
}
