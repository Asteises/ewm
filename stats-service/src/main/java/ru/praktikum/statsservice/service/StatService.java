package ru.praktikum.statsservice.service;

import org.springframework.stereotype.Service;
import ru.praktikum.statsservice.model.dto.EndpointHitDto;

@Service
public interface StatService {

    void save(EndpointHitDto endpointHitDto);

    Integer getEventStatInfo(long eventId);
}
