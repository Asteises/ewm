package ru.praktikum.statsservice;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.praktikum.statsservice.model.EndpointHit;
import ru.praktikum.statsservice.model.dto.EndpointHitDto;

@Service
@RequiredArgsConstructor
public class StatServiceImpl implements StatService {

    private final StatStorage statStorage;

    @Override
    public void save(EndpointHitDto endpointHitDto) {
        EndpointHit endpointHit = StatMapper.toEndpointHit(endpointHitDto);
        statStorage.save(endpointHit);
    }

    @Override
    public Integer getEventStatInfo(long eventId) {
        return statStorage.getViewsEventById(eventId);
    }
}
