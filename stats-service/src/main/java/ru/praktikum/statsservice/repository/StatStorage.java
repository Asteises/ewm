package ru.praktikum.statsservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.praktikum.statsservice.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatStorage extends JpaRepository<EndpointHit, Long> {

    List<EndpointHit> findAllByCreatedBetween(LocalDateTime start, LocalDateTime end);

    List<EndpointHit> findAllByUriAndCreatedBetween(String uri, LocalDateTime start, LocalDateTime end);
}
