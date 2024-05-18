package com.pronsky.tickets.service;

import com.pronsky.tickets.service.dto.FlightDto;

import java.util.List;

public interface FlightService {

    void save();
    List<FlightDto> flights(String departure, String destination);
    void saveFile(String fileUrl, String fileName);
}
