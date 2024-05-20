package com.pronsky.tickets.data.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
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

    @Column(name = "departure_date")
    private LocalDate departureDate;

    @Column(name = "departure_time")
    private LocalTime departureTime;

    @Column(name = "arrival_date")
    private LocalDate arrivalDate;

    @Column(name = "arrival_time")
    private LocalTime arrivalTime;

    @Column(name = "carrier")
    private String carrier;

    @Column(name = "stops")
    private byte stops;

    @Column(name = "price")
    private int price;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Flight flight = (Flight) o;
        return stops == flight.stops && price == flight.price && Objects.equals(id, flight.id) && Objects.equals(origin, flight.origin) && Objects.equals(originName, flight.originName) && Objects.equals(destination, flight.destination) && Objects.equals(destinationName, flight.destinationName) && Objects.equals(departureDate, flight.departureDate) && Objects.equals(departureTime, flight.departureTime) && Objects.equals(arrivalDate, flight.arrivalDate) && Objects.equals(arrivalTime, flight.arrivalTime) && Objects.equals(carrier, flight.carrier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, origin, originName, destination, destinationName, departureDate, departureTime, arrivalDate, arrivalTime, carrier, stops, price);
    }

    @Override
    public String toString() {
        return "Flight{" +
                "id=" + id +
                ", origin='" + origin + '\'' +
                ", originName='" + originName + '\'' +
                ", destination='" + destination + '\'' +
                ", destinationName='" + destinationName + '\'' +
                ", departureDate=" + departureDate +
                ", departureTime=" + departureTime +
                ", arrivalDate=" + arrivalDate +
                ", arrivalTime=" + arrivalTime +
                ", carrier='" + carrier + '\'' +
                ", stops=" + stops +
                ", price=" + price +
                '}';
    }
}
