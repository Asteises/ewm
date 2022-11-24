package ru.praktikum.mainservice.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.praktikum.mainservice.client.StatClient;
import ru.praktikum.mainservice.event.mapper.EventMapper;
import ru.praktikum.mainservice.event.model.dto.EventFullDto;
import ru.praktikum.mainservice.event.model.dto.EventShortDto;
import ru.praktikum.mainservice.event.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class EventPublicController {

    private final EventService eventService;
    private final StatClient statClient;

    // TODO
    /*
    GET EVENTS - Получение событий с возможностью фильтрации
        Обратите внимание:
            + это публичный эндпоинт, соответственно в выдаче должны быть только опубликованные события;
            + текстовый поиск (по аннотации и подробному описанию) должен быть без учета регистра букв;
            если в запросе не указан диапазон дат [rangeStart-rangeEnd], то нужно выгружать события, которые произойдут позже текущей даты и времени;
            информация о каждом событии должна включать в себя количество просмотров и количество уже одобренных заявок на участие;
            информацию о том, что по этому эндпоинту был осуществлен и обработан запрос, нужно сохранить в сервисе статистики;
     */
    @GetMapping()
    public List<EventShortDto> getAllPublicEvents(@RequestParam String text,
                                                  @RequestParam Long[] categories,
                                                  @RequestParam Boolean paid,
                                                  @RequestParam String rangeStart,
                                                  @RequestParam String rangeEnd,
                                                  @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                                  @RequestParam String sort, // Вариант сортировки: по дате события или по количеству просмотров Available values : EVENT_DATE, VIEWS
                                                  @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                  @Positive @RequestParam(defaultValue = "10") Integer size,
                                                  HttpServletRequest request) {

        log.info("Получаем все события с учетом фильтрации: text={}, categories={}, paid={}, rangeStart={}, " +
                        "rangeEnd={}, onlyAvailable={}, sort={}, from={}, size={}",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);

        LocalDateTime start = LocalDateTime.parse(rangeStart, EventMapper.FORMATTER_EVENT_DATE);
        LocalDateTime end = LocalDateTime.parse(rangeEnd, EventMapper.FORMATTER_EVENT_DATE);

        // Информация для сервиса статистики;
        log.info("client ip: {}", request.getRemoteAddr());
        log.info("endpoint path: {}", request.getRequestURI());
        statClient.saveRequestInfo(request);

        List<EventShortDto> result = eventService.getAllPublicEvents(
                text,
                Arrays.stream(categories).toList(),
                paid,
                rangeStart,
                rangeEnd,
                sort,
                from,
                size);

        for (EventShortDto eventShortDto : result) {
            Object o = statClient.getStatsByEventId(eventShortDto.getId());
            Integer views = (Integer) o;
            eventShortDto.setViews(views);
        }
        return result;
    }

    /*
    Получение подробной информации об опубликованном событии по его идентификатору
        Обратите внимание:
            событие должно быть опубликовано;
            информация о событии должна включать в себя количество просмотров и количество подтвержденных запросов;
            информацию о том, что по этому эндпоинту был осуществлен и обработан запрос, нужно сохранить в сервисе статистики;
     */
    @GetMapping("/{id}")
    public EventFullDto getPublicEventById(@PathVariable long id,
                                           HttpServletRequest request) {

        log.info("Получаем событие: id={}", id);

        // Информация для сервиса статистики;
        log.info("client ip: {}", request.getRemoteAddr());
        log.info("endpoint path: {}", request.getRequestURI());
        statClient.saveRequestInfo(request);

        Integer views = (Integer) statClient.getStatsByEventId(id).getBody();

        EventFullDto eventFullDto = eventService.getPublicEventById(id);
        eventFullDto.setViews(views);

        return eventFullDto;
    }
}
