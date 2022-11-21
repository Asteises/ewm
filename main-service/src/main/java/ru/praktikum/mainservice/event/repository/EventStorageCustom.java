package ru.praktikum.mainservice.event.repository;

import org.springframework.data.domain.Pageable;
import ru.praktikum.mainservice.event.model.Event;
import ru.praktikum.mainservice.event.model.dto.EventPublicFilterDto;

import java.util.List;

public interface EventStorageCustom {

    // Ищем все опубликованные события с заданными параметрами;
    List<Event> findAllEventsByFilterParams(EventPublicFilterDto eventPublicFilterDto, Pageable pageable);
}
