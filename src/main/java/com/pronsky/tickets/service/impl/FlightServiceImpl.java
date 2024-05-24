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
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FlightServiceImpl implements FlightService {
    private static final String ORIGIN = "VVO";
    private static final String DESTINATION = "TLV";
    private static final String FILE_NAME = "/data/tickets.json";
    private static final String VVO_ZONE_ID = "+10";
    private static final String TLV_ZONE_ID = "+3";

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
        List<FlightDto> flights = repository.findAllByOriginAndDestination(ORIGIN, DESTINATION)
                .stream()
                .map(mapper::toDto)
                .toList();
        Map<String, Duration> result = new HashMap<>();
        String carrier;
        for (FlightDto flight : flights) {
            carrier = flight.getCarrier();
            if (!result.containsKey(carrier)) {
                result.put(carrier, null);
            }
        }
        Duration duration;
        for (FlightDto flight : flights) {
            for (String key : result.keySet()) {
                Duration value = result.get(key);
                ZonedDateTime departure = parseDateTime(flight.getDepartureDateAndTime(), ZoneId.of(VVO_ZONE_ID));
                ZonedDateTime arrival = parseDateTime(flight.getArrivalDateAndTime(), ZoneId.of(TLV_ZONE_ID));
                duration = Duration.between(departure, arrival);
                if (flight.getCarrier().equals(key) && (value == null || duration.compareTo(value) < 0)) {
                    result.put(key, duration);
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
        String date = String.valueOf(node.get(dateProperty)).replaceAll("\"", "");
        String time = String.valueOf(node.get(timeProperty)).replaceAll("\"", "");
        if (time.length() == 4) {
            time = "0" + time;
        }
        return date + " " + time;
    }

    private ZonedDateTime parseDateTime(String dateTime, ZoneId zoneId) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");
        LocalDateTime localDateTime = LocalDateTime.parse(dateTime, formatter);
        return ZonedDateTime.of(localDateTime, zoneId);
    }

    private String reformatToUTF(String raw) {
        return Arrays.toString(raw.replaceAll("\"", "").getBytes(StandardCharsets.UTF_8));
    }
}
