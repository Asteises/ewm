package ru.praktikum.mainservice.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.praktikum.mainservice.client.dto.EndpointHitDto;
import ru.praktikum.mainservice.event.mapper.EventMapper;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class StatClient extends BaseClient {

    @Autowired
    public StatClient(@Value("${ewm-stats-service.url}") String url, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(url))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public void saveRequestInfo(HttpServletRequest httpServletRequest) {

        EndpointHitDto endpointHitDto = new EndpointHitDto();
        endpointHitDto.setApp("main-service");
        endpointHitDto.setUri(httpServletRequest.getRequestURI());
        endpointHitDto.setIp(httpServletRequest.getRemoteAddr());
        endpointHitDto.setTimestamp(LocalDateTime.now().format(EventMapper.FORMATTER_EVENT_DATE));

        ResponseEntity<Object> response = post("/hit", endpointHitDto);
    }

    public ResponseEntity<Object> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {

        Map<String, Object> parameters = Map.of(
                "start", start.format(EventMapper.FORMATTER_EVENT_DATE),
                "end", end.format(EventMapper.FORMATTER_EVENT_DATE),
                "uris", uris,
                "unique", unique
        );

        log.info("parameters={}", parameters);

        ResponseEntity<Object> response = get("/stats?start={start}&end={end}&uris={uris}&unique={unique}", parameters);
        log.info("response={}", response);
        return response;
    }

    public ResponseEntity<Object> getStatsByEventId(long eventId) {
        String path = "/stats/" + eventId;

        Map<String, Object> parametrs = Map.of(
                "start", LocalDateTime.MIN,
                "end", LocalDateTime.now(),
                "uri", path,
                "unique", false
        );

        ResponseEntity<Object> response = get(path, parametrs);

        log.info("Получаем статистику просмотров для eventId={}: response={}", eventId, response.getBody());
        return response;
    }

}
