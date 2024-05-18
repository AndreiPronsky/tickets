package com.pronsky.tickets.service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.util.Date;

@Data
@NoArgsConstructor
public class FlightDto {
    private Long id;

    private String origin;

    private String originName;

    private String destination;

    private String destinationName;

    private Date departureDate;

    private Time departureTime;

    private Date arrivalDate;

    private Time arrivalTime;

    private String carrier;

    private byte stops;

    private int price;
}
