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
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class FlightServiceImpl implements FlightService {
    private static final String FILE_URL = "https://s05klg.storage.yandex.net/rdisk/15e92e3d4834583b10e0153c2c777820cb0bce92796f3a81274a84d6e78ca4fc/6648e326/tYeYlYQMDWrLhsxckWkAuP3Ck9Wv7deEj87WcHGwdTm49fctSgbzwkL8qSfsWKghDzP_RaLgpvN1s69sv-aM0g==?uid=0&filename=tickets.json&disposition=attachment&hash=gxbPmhnihZEHDf40Y%2BmEflWv/UCAvstx8Z40Bynq9aBVoadN0nT%2BBKoi87wK3wOGq/J6bpmRyOJonT3VoXnDag%3D%3D&limit=0&content_type=text%2Fplain&owner_uid=332089970&fsize=3922&hid=bc0d0d9d411c6f4a469ecad58e277e93&media_type=text&tknv=v2&ts=618bdac029580&s=d7f940c8a723271b3bb56060761ed1e759fc533fbf58a5fb64aa917db19eae3a&pb=U2FsdGVkX1_R9rrvKDRlTBO1Nyn4cimwQwBfrZwR7TklyGLbKqop2LGXg3j0mVBe_newNdG9mg_FBQnEJ4yYOTvDgf9y3iP80QOidtvKspQ";
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
        List<FlightDto> flights = repository.findAllByDepartureAndDestination(origin, destination)
                .stream()
                .map(mapper::toDto)
                .toList();
        Double median = getMedian(flights);
        Double average = getAverage(flights);
        return median - average;
    }

    @Override
    public Map<String, Duration> getMinDurationForEveryCarrier(List<FlightDto> flights) {
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

    private List<FlightDto> deserialize() {
        fileUtil.saveFile(FILE_URL, FILE_NAME);
        String content = fileUtil.readFile(FILE_NAME);
        List<FlightDto> flights = new ArrayList<>();
        try {
            JsonNode root = objectMapper.readTree(content);
            JsonNode tickets = root.get("tickets");
            if (tickets.isArray()) {
                for (JsonNode node : tickets) {
                    FlightDto dto = new FlightDto();
                    dto.setOrigin(String.valueOf(node.get("origin")));
                    dto.setOriginName(String.valueOf(node.get("origin_name")));
                    dto.setDestination(String.valueOf(node.get("destination")));
                    dto.setDestinationName(String.valueOf(node.get("destination_name")));
                    dto.setDepartureDate(LocalDate.parse(String.valueOf(node.get("departure_date"))));
                    dto.setDepartureTime(LocalTime.parse(String.valueOf(node.get("departure_time"))));
                    dto.setArrivalDate(LocalDate.parse(String.valueOf(node.get("arrival_date"))));
                    dto.setArrivalTime(LocalTime.parse(String.valueOf(node.get("arrival_time"))));
                    dto.setCarrier(String.valueOf(node.get("carrier")));
                    dto.setStops(Byte.parseByte(String.valueOf(node.get("stops"))));
                    dto.setPrice(Integer.parseInt(String.valueOf(node.get("price"))));
                    flights.add(dto);
                }
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
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
