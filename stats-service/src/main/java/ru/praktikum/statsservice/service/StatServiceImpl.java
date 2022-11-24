package ru.praktikum.statsservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import one.util.streamex.StreamEx;
import org.springframework.stereotype.Service;
import ru.praktikum.statsservice.model.dto.ViewStatsDto;
import ru.praktikum.statsservice.repository.StatStorage;
import ru.praktikum.statsservice.mapper.StatMapper;
import ru.praktikum.statsservice.model.EndpointHit;
import ru.praktikum.statsservice.model.dto.EndpointHitDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatServiceImpl implements StatService {

    private final StatStorage statStorage;

    public static final DateTimeFormatter FORMATTER_EVENT_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void save(EndpointHitDto endpointHitDto) {

        // Создаем EndpointHit, мапим его и сохраняем в БД;
        EndpointHit endpointHit = StatMapper.toEndpointHit(endpointHitDto);

        log.info("Сохранили endpointHit={}", endpointHitDto);
        statStorage.save(endpointHit);
    }

    @Override
    public List<ViewStatsDto> getEventsStatInfo(String start, String end, List<String> uris, Boolean unique) {

        // Парсим LocalDateTime из String;
        LocalDateTime currentStart = LocalDateTime.parse(start, FORMATTER_EVENT_DATE);
        LocalDateTime currentEnd = LocalDateTime.parse(end, FORMATTER_EVENT_DATE);

        // Создаем результирующий объект;
        List<ViewStatsDto> viewStatsDtos = new ArrayList<>();

        // Если список uris пришел пустой, то запишем в него все uri которые есть в БД ля данного приложения в диапазоне времени;
        if (uris == null) {
            uris = statStorage.findAllByCreatedBetween(currentStart, currentEnd)
                    .stream()
                    .map(EndpointHit::getUri)
                    .distinct()
                    .collect(Collectors.toList());
        }

        // Промежуточный лист EndpointHit для простоты восприятия;
        List<EndpointHit> endpointHits;

        // Проходимся по списку uris, чтобы посчитать просмотры для каждого;
        for (String uri: uris) {

            // Создаем ViewStatsDto в который будем записывать данные;
            ViewStatsDto viewStatsDto = new ViewStatsDto();

            // Сетим uri;
            viewStatsDto.setUri(uri);

            // Находим все EndpointHit по заданным параметрам;
            endpointHits = statStorage.findAllByUriAndCreatedBetween(uri, currentStart, currentEnd);

            // Если нужны данные с уникальными ip, то сначала обновим лист endpointHits;
            if (unique) {

                // Воспользуемся StreamEx, чтобы оставить в списке только уникальные значения;
                endpointHits = StreamEx.of(endpointHits)
                        .distinct(EndpointHit::getIp)
                        .collect(Collectors.toList());
            }

            // Сетим количество просмотров;
            viewStatsDto.setHits((Integer) endpointHits.size());
        }

        log.info("Получаем ViewStats={}", viewStatsDtos);
        return viewStatsDtos;
    }

    @Override
    public Integer findAllByUri(String uri, String start, String end) {

        // Парсим LocalDateTime из String;
        LocalDateTime currentStart = LocalDateTime.parse(start, FORMATTER_EVENT_DATE);
        LocalDateTime currentEnd = LocalDateTime.parse(end, FORMATTER_EVENT_DATE);

        return statStorage.findAllByUriAndCreatedBetween(uri, currentStart, currentEnd).size();
    }
}
