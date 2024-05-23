package com.pronsky.tickets.data;

import com.pronsky.tickets.data.entities.Flight;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Lazy
@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {
    @Query("FROM Flight f WHERE f.origin = :origin AND f.destination = :destination")
    List<Flight> findAllByOriginAndDestination(String origin, String destination);
}
