package ru.praktikum.statsservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.praktikum.statsservice.StatService;
import ru.praktikum.statsservice.model.dto.EndpointHitDto;
import ru.praktikum.statsservice.model.dto.ViewStatsDto;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/")
public class StatisticController {

    private final StatService statService;

    /*
    POST - Сохранение информации о том, что к эндпоинту был запрос
        Сохранение информации о том, что на uri конкретного сервиса был отправлен запрос пользователем.
        Название сервиса, uri и ip пользователя указаны в теле запроса.
     */
    @PostMapping("/hit")
    public void saveRequestInfo(@RequestBody EndpointHitDto endpointHitDto) {

        log.info("Сохранение информации о том, что к эндпоинту uri={} был запрос: app={}",
                endpointHitDto.getUri(), endpointHitDto.getApp());
        statService.save(endpointHitDto);
    }

    /*
    GET - Получение статистики по посещениям. Обратите внимание: значение даты и времени нужно закодировать
    (например используя java.net.URLEncoder.encode)
     */
    @GetMapping("/stats")
    public List<ViewStatsDto> getEventsStatInfo(@RequestParam String start,
                                                @RequestParam String end,
                                                @RequestParam String[] uris,
                                                @RequestParam(defaultValue = "false") Boolean unique) {

        log.info("Получаем статистику на события: uris={} с параметрами start={}, end={}, unique={}",
                uris, start, end, unique);


        return null;
    }

    @GetMapping("/stats/{eventId}")
    public Integer getEventStatInfo(@PathVariable long eventId) {

        log.info("Получаем статистику на событие: eventId={}", eventId);
        return statService.getEventStatInfo(eventId);
    }
}
