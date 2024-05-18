package com.pronsky.tickets.data;

import com.pronsky.tickets.data.entities.Flight;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FlightRepository extends JpaRepository<Flight, Long> {
    List<Flight> findAllByDepartureAndDestination(String departure, String destination);
}
