package ru.praktikum.mainservice.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.praktikum.mainservice.client.StatClient;
import ru.praktikum.mainservice.event.mapper.EventMapper;
import ru.praktikum.mainservice.event.model.dto.AdminUpdateEventRequest;
import ru.praktikum.mainservice.event.model.dto.EventFullDto;
import ru.praktikum.mainservice.event.service.EventService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/events")
public class EventAdminController {

    private final EventService eventService;

    private final StatClient statClient;

    // TODO
    /*
    GET EVENT ADMIN - Поиск событий.
        Эндпоинт возвращает полную информацию обо всех событиях подходящих под переданные условия;
     */
    @GetMapping
    public List<EventFullDto> searchEvents(@RequestParam Long[] users,
                                           @RequestParam String[] states,
                                           @RequestParam Long[] categories,
                                           @RequestParam String rangeStart,
                                           @RequestParam String rangeEnd,
                                           @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                           @Positive @RequestParam(defaultValue = "10") Integer size) {


        LocalDateTime start = LocalDateTime.parse(rangeStart, EventMapper.FORMATTER_EVENT_DATE);
        LocalDateTime end = LocalDateTime.parse(rangeEnd, EventMapper.FORMATTER_EVENT_DATE);

        log.info("Получаем все события с учетом параметров: users={}, states={}, categories={}, " +
                        "rangeStart={}, rangeEnd={}, from={}, size={}",
                Arrays.toString(users),
                Arrays.toString(states),
                Arrays.toString(categories),
                rangeStart,
                rangeEnd,
                from,
                size);

        List<EventFullDto> result = eventService.searchEvents(
                users,
                states,
                categories,
                rangeStart,
                rangeEnd,
                from,
                size);

        for (EventFullDto eventFullDto : result) {
            Integer views = (Integer) statClient.getStatsByEventId(eventFullDto.getId());
            eventFullDto.setViews(views);
        }

        return result;
    }

    /*
    PUT EVENT ADMIN - Редактирование события.
        Редактирование данных любого события администратором. Валидация данных не требуется;
    */
    @PutMapping("/{eventId}")
    public EventFullDto updateEventByAdmin(@PathVariable long eventId,
                                           @RequestBody AdminUpdateEventRequest adminUpdateEventRequest) {

        log.info("Админ редактирует событие: eventId={}", eventId);
        return eventService.updateEventByAdmin(eventId, adminUpdateEventRequest);
    }

    /*
    PUT EVENT ADMIN - Публикация события.
        Обратите внимание:
            + дата начала события должна быть не ранее чем за час от даты публикации;
            + событие должно быть в состоянии ожидания публикации;
    */
    @PatchMapping("/{eventId}/publish")
    public EventFullDto eventPublishByAdmin(@PathVariable long eventId) {

        log.info("Админ подтверждает событие и публикует его: eventId={}", eventId);
        return eventService.eventPublishByAdmin(eventId);
    }

    /*
    PUT EVENT ADMIN - Отклонение события.
        Обратите внимание:
            + событие не должно быть опубликовано;
    */
    @PatchMapping("/{eventId}/reject")
    public EventFullDto eventRejectByAdmin(@PathVariable long eventId) {

        log.info("Админ отклоняет событие и публикует его: eventId={}", eventId);
        return eventService.eventRejectByAdmin(eventId);
    }
}
