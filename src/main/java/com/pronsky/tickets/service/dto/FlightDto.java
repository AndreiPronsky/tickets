package com.pronsky.tickets.service.dto;

import jakarta.persistence.Column;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;;
import java.sql.Date;

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
