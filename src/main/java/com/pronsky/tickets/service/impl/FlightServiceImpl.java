package com.pronsky.tickets.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pronsky.tickets.data.FlightRepository;
import com.pronsky.tickets.service.FlightMapper;
import com.pronsky.tickets.service.FlightService;
import com.pronsky.tickets.service.dto.FlightDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FlightServiceImpl implements FlightService {
    private static final String ORIGIN = "VVO";
    private static final String DESTINATION = "TLV";
    private static final String FILE_NAME = "/data/tickets.json";
    private final FlightRepository repository;
    private final ObjectMapper objectMapper;
    private final FlightMapper mapper;

    @Override
    public void saveAll(List<FlightDto> flights) {
        for (FlightDto flightDto : deserialize()) {
            repository.save(mapper.toEntity(flightDto));
        }
    }

    @Override
    public Double getDifferenceBtwMedianAndAverage() {
        List<FlightDto> flights = repository.findAllByOriginAndDestination(ORIGIN, DESTINATION)
                .stream()
                .map(mapper::toDto)
                .toList();
        Double median = getMedian(flights);
        Double average = getAverage(flights);
        return median - average;
    }

    @Override
    public Map<String, Duration> getMinDurationForEveryCarrier() {
        List<FlightDto> flights = deserialize();
        Map<String, Duration> result = new HashMap<>();
        String carrier;
        for (FlightDto flight : flights) {
            carrier = flight.getCarrier();
            if (!result.containsKey(carrier)) {
                result.put(carrier, null);
            }
        }
        Duration duration;
        for (String key : result.keySet()) {
            Duration value = result.get(key);
            for (FlightDto flight : flights) {
                LocalDateTime departure = parseDateTime(flight.getDepartureDateAndTime());
                LocalDateTime arrival = parseDateTime(flight.getArrivalDateAndTime());
                duration = Duration.between(departure, arrival);
                if (flight.getCarrier().equals(key) && (value == null || duration.compareTo(result.get(key)) < 0)) {
                    result.replace(key, duration);
                }
            }
        }
        return result;
    }

    @Override
    public List<FlightDto> deserialize() {
        List<FlightDto> flights = new ArrayList<>();
        try (InputStream is = getClass().getResourceAsStream(FILE_NAME)) {
            String content = new String(is.readAllBytes()).replaceAll("\uFEFF", "");
            JsonNode root = objectMapper.readTree(content);
            JsonNode tickets = root.get("tickets");
            if (tickets.isArray()) {
                for (JsonNode node : tickets) {
                    setValuesAndAddToList(flights, node);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return flights;
    }

    private Double getMedian(List<FlightDto> flights) {
        List<Integer> prices = new ArrayList<>();
        for (FlightDto flight : flights) {
            prices.add(flight.getPrice());
        }
        Collections.sort(prices);
        Double median;
        if (prices.size() % 2 != 0) {
            median = Double.valueOf(prices.get(prices.size() / 2));
        } else {
            median = (double) ((prices.get(prices.size() / 2 - 1) + (prices.get(prices.size() / 2))) / 2);
        }
        return median;
    }

    private Double getAverage(List<FlightDto> flights) {
        double sum = 0.0;
        for (FlightDto flight : flights) {
            sum += flight.getPrice();
        }
        return sum / flights.size();
    }

    private void setValuesAndAddToList(List<FlightDto> flights, JsonNode node) {
        FlightDto dto = new FlightDto();
        dto.setOrigin(String.valueOf(node.get("origin")).replaceAll("\"", ""));
        dto.setOriginName(reformatToUTF(String.valueOf(node.get("origin_name"))));
        dto.setDestination(String.valueOf(node.get("destination")).replaceAll("\"", ""));
        dto.setDestinationName(reformatToUTF(String.valueOf(node.get("destination_name"))));
        dto.setDepartureDateAndTime(reformatDateAndTime(node, "departure_date", "departure_time"));
        dto.setArrivalDateAndTime(reformatDateAndTime(node, "arrival_date", "arrival_time"));
        dto.setCarrier(String.valueOf(node.get("carrier")));
        dto.setStops(Byte.parseByte(String.valueOf(node.get("stops"))));
        dto.setPrice(Integer.parseInt(String.valueOf(node.get("price"))));
        flights.add(dto);
    }

    private String reformatDateAndTime(JsonNode node, String dateProperty, String timeProperty) {
        String departureDate = String.valueOf(node.get(dateProperty)).replaceAll("\"", "");
        String departureTime = String.valueOf(node.get(timeProperty)).replaceAll("\"", "");
        if (departureTime.length() == 4) {
            departureTime = "0" + departureTime;
        }
        return departureDate + " " + departureTime;
    }

    private LocalDateTime parseDateTime(String dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");
        return LocalDateTime.parse(dateTime, formatter);
    }

    private String reformatToUTF(String raw) {
        return String.valueOf(raw.replaceAll("\"", "").getBytes(StandardCharsets.UTF_8));
    }
}
