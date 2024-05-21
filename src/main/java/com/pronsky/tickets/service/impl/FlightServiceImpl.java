package com.pronsky.tickets.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pronsky.tickets.data.FlightRepository;
import com.pronsky.tickets.service.FlightMapper;
import com.pronsky.tickets.service.FlightService;
import com.pronsky.tickets.service.dto.FlightDto;
import com.pronsky.tickets.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class FlightServiceImpl implements FlightService {
    private static final String FILE_URL = "https://s388vla.storage.yandex.net/rdisk/939a7cca7b7e05bd60c4fc7621d5cd6de35bca8a8eba9f7d01540af03456a76f/664ca6cf/tYeYlYQMDWrLhsxckWkAuKmyFwxd2DOwscmTzQm-lQbsHtZjDZnhMs2ptGNi23tcVzdDAF_DH6yVOzUgfaC8pQ==?uid=0&filename=%D0%A2%D0%B5%D1%81%D1%82%D0%BE%D0%B2%D0%BE%D0%B5_%D0%B7%D0%B0%D0%B4%D0%B0%D0%BD%D0%B8%D0%B5_%D1%80%D0%B0%D0%B7%D1%80%D0%B0%D0%B1%D0%BE%D1%82%D1%87%D0%B8%D0%BA_%D0%BD%D0%B0_%D0%BF%D0%BB%D0%B0%D1%82%D1%84%D0%BE%D1%80%D0%BC%D0%B5.docx&disposition=attachment&hash=1ReiRneGK1xcK7wl9uf2I3enG0fxygtNyklRyyD7cZUGhnCo7VrUKeIwApEvapxnq/J6bpmRyOJonT3VoXnDag%3D%3D&limit=0&content_type=application%2Fvnd.openxmlformats-officedocument.wordprocessingml.document&owner_uid=332089970&fsize=27585&hid=20cedbc619a03cba4dc7499d18a6af14&media_type=document&tknv=v2&ts=618f71c4c11c0&s=e45590fe8905ab6c66ffa557a7d94ad7714db6b95c78fd0d5f4d83e4e90f202a&pb=U2FsdGVkX18NY-2eGoZRUv6A96THMgi6f2rfXl0Jvy9xOtFl9QEtETOAIzng6cbeCDUjiorA-VHf0S0l7AHl7AV7iVq2EYYlfJCxapoUCvI";
    private static final String FILE_NAME = "tickets.json";
    private static final String origin = "VVO";
    private static final String destination = "TLV";
    private final FlightRepository repository;
    private final ObjectMapper objectMapper;
    private final FlightMapper mapper;
    private final FileUtil fileUtil;

    @Override
    public void saveAll(List<FlightDto> flights) {
        for (FlightDto flightDto : deserialize()) {
            repository.save(mapper.toEntity(flightDto));
        }
    }

    @Override
    public Double getDifferenceBtwMedianAndAverage() {
        List<FlightDto> flights = repository.findAllByOriginAndDestination(origin, destination)
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
                duration = Duration.between(flight.getArrivalTime(), flight.getDepartureTime());
                if (flight.getCarrier().equals(key) && (duration.compareTo(result.get(key)) < 0 || value == null)) {
                    result.replace(key, duration);
                }
            }
        }
        return result;
    }

    @Override
    public List<FlightDto> deserialize() {
        fileUtil.saveFile(FILE_URL, FILE_NAME);
        String content = fileUtil.readFile(FILE_NAME);
        List<FlightDto> flights = new ArrayList<>();
        try {
            JsonNode root = objectMapper.readTree(content);
            JsonNode tickets = root.get("tickets");
            if (tickets.isArray()) {
                for (JsonNode node : tickets) {
                    setValuesAndAddToList(flights, node);
                }
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return flights;
    }

    private void setValuesAndAddToList(List<FlightDto> flights, JsonNode node) {
        FlightDto dto = new FlightDto();
        dto.setOrigin(String.valueOf(node.get("origin")));
        dto.setDestination(String.valueOf(node.get("destination")));
        dto.setDepartureDate(LocalDate.parse(String.valueOf(node.get("departure_date"))));
        dto.setDepartureTime(LocalTime.parse(String.valueOf(node.get("departure_time"))));
        dto.setArrivalDate(LocalDate.parse(String.valueOf(node.get("arrival_date"))));
        dto.setArrivalTime(LocalTime.parse(String.valueOf(node.get("arrival_time"))));
        dto.setCarrier(String.valueOf(node.get("carrier")));
        dto.setStops(Byte.parseByte(String.valueOf(node.get("stops"))));
        dto.setPrice(Integer.parseInt(String.valueOf(node.get("price"))));
        flights.add(dto);
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
            median = ((double) (prices.get(prices.size() / 2 - 1) + (prices.get(prices.size() / 2)) / 2));
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
}
