package ru.praktikum.mainservice.event.repository;

import org.springframework.data.domain.Pageable;
import ru.praktikum.mainservice.event.model.Event;
import ru.praktikum.mainservice.event.model.dto.EventPublicFilterDto;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EventStorageCustomImpl implements EventStorageCustom {

    // Для корректного открытия и закрытия EntityManager;
    @PersistenceContext
    private EntityManager entityManager;

    // Ищем только опубликованные события: State - PUBLISHED
    @Override
    public List<Event> findAllEventsByFilterParams(EventPublicFilterDto dto, Pageable pageable) {

        // Создаем менеджер для работы с предикатами;
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<Event> query = cb.createQuery(Event.class);

        Root<Event> event = query.from(Event.class);

        // Создаем список куда будем складывать предикаты;
        List<Predicate> predicates = getPredicates(dto, cb, event);

        Predicate eventPredicate = cb.and(predicates.toArray(new Predicate[0]));

        query.where(eventPredicate);

        // // Вариант сортировки: по дате события или по количеству просмотров Available values : EVENT_DATE, VIEWS
        if (dto.getSort().equals("EVENT_DATE")) {
            query.orderBy(cb.desc(event.get("eventDate")));
        } else if (dto.getSort().equals("VIEWS")) {
            query.orderBy(cb.asc(event.get("eventDate")));
        }

        TypedQuery<Event> typedQuery = entityManager.createQuery(query);
        return typedQuery.getResultList();
    }

    private List<Predicate> getPredicates(EventPublicFilterDto eventDto,
                                          CriteriaBuilder cb,
                                          Root<Event> event) {

        // Создаем лист предикатов;
        List<Predicate> predicates = new ArrayList<>();

        // Далее проверяем каждый параметр;
        if (eventDto.getState() != null) {
            Predicate statePredicate = cb.equal(event.get("state"), eventDto.getState());
            predicates.add(statePredicate);
        }

        if (eventDto.getText() != null) {
            Predicate textAnnotationPredicate = cb.equal(event.get("event").get("annotation"), eventDto.getText());
            predicates.add(textAnnotationPredicate);

            Predicate textDescriptionPredicate = cb.equal(event.get("event").get("description"), eventDto.getText());
            predicates.add(textDescriptionPredicate);
        }

        if (eventDto.getCategories() != null) {
            List<Long> catIds = Arrays.asList(eventDto.getCategories());

            Expression<Long> catEventIds = event.get("event").get("category").get("id");

            Predicate predicateCatIn = catEventIds.in(catIds);
            predicates.add(predicateCatIn);
        }

        if (eventDto.getPaid() != null) {
            Predicate paidPredicate = cb.equal(event.get("event").get("paid"), eventDto.getPaid());
            predicates.add(paidPredicate);
        }

        if (eventDto.getRangeStart() != null && eventDto.getRangeEnd() != null) {
            Predicate rangeStartEndPredicate = cb.between(event.get("createdOn"),
                    eventDto.getRangeStart(),
                    eventDto.getRangeEnd());
            predicates.add(rangeStartEndPredicate);
        } else {
            Predicate rangeStartPredicate = cb.greaterThan(event.get("createdOn"),
                    eventDto.getRangeStart());
            predicates.add(rangeStartPredicate);
        }
        return predicates;
    }
}
