package com.pronsky.tickets.service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FlightDto {
    private Long id;

    private String origin;

    private String originName;

    private String destination;

    private String destinationName;

    private String departureDateAndTime;

    private String arrivalDateAndTime;

    private String carrier;

    private byte stops;

    private int price;
}
