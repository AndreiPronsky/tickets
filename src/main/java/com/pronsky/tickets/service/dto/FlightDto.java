package com.pronsky.tickets.service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
public class FlightDto {
    private Long id;

    private String origin;

    private String originName;

    private String destination;

    private String destinationName;

    private LocalDate departureDate;

    private LocalTime departureTime;

    private LocalDate arrivalDate;

    private LocalTime arrivalTime;

    private String carrier;

    private byte stops;

    private int price;
}
