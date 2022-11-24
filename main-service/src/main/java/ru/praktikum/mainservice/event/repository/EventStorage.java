package ru.praktikum.mainservice.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.praktikum.mainservice.event.model.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventStorage extends JpaRepository<Event, Long>, EventStorageCustom {

    Page<Event> findEventByInitiator_Id(long userId, Pageable pageable);

    Optional<Event> findEventByCategory_Id(long catId);

    List<Event> findEventsByIdIn(List<Long> eventIds);

    @Query("select e from Event as e " +
            "where ((:text) is null " +
            "or lower(e.annotation) like lower(concat('%', :text, '%') )" +
            "or lower(e.description) like lower(concat('%', :text, '%') ) )" +
            "and ((:categories) is null or e.category.id in :categories)" +
            "and ((:paid) is null or e.paid = :paid)" +
            "and (e.eventDate >= :start)" +
            "and (e.eventDate <= :end)")
    Page<Event> findAllPublicEvents(
            String text,
            List<Long> categories,
            Boolean paid,
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable);

    @Query("SELECT e FROM Event AS e " +
            "WHERE ((:users) IS NULL OR e.initiator.id IN :users) " +
            "AND ((:states) IS NULL OR e.state IN :states) " +
            "AND ((:categories) IS NULL OR e.category.id IN :categories) " +
            "AND (e.eventDate >= :rangeStart) " +
            "AND ( e.eventDate <= :rangeEnd)")
    Page<Event> findEventsByAdminSearch(
            List<Long> users,
            List<Long> categories,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            List<String> states,
            Pageable pageable);

    Page<Event> findAllByInitiator_IdInAndCategory_IdInAndEventDateBetweenAndStateIn(
            List<Long> users,
            List<Long> categories,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            List<String> states,
            Pageable pageable);
}
