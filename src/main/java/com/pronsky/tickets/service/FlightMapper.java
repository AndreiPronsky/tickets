package com.pronsky.tickets.service;

import com.pronsky.tickets.data.entities.Flight;
import com.pronsky.tickets.service.dto.FlightDto;
import org.mapstruct.Mapper;
import org.springframework.context.annotation.Lazy;

@Lazy
@Mapper(componentModel = "spring")
public interface FlightMapper {
    FlightDto toDto(Flight entity);
    Flight toEntity(FlightDto dto);
}
