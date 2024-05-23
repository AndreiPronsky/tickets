package com.pronsky.tickets.data.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Time;
import java.sql.Date;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "flights")
public class Flight {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "origin")
    private String origin;

    @Column(name = "origin_name")
    private String originName;

    @Column(name = "destination")
    private String destination;

    @Column(name = "destination_name")
    private String destinationName;

    @Column(name = "dep_date_and_time")
    private String departureDateAndTime;

    @Column(name = "arrival_date_and_time")
    private String arrivalDateAndTime;

    @Column(name = "carrier")
    private String carrier;

    @Column(name = "stops")
    private byte stops;

    @Column(name = "price")
    private int price;

}
