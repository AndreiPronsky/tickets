package com.pronsky.tickets.service;

import com.pronsky.tickets.service.dto.FlightDto;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public interface FlightService {

    void saveAll(List<FlightDto> flightDtos);
    Double getDifferenceBtwMedianAndAverage();

    Map<String, Duration> getMinDurationForEveryCarrier(List<FlightDto> flights);
}
