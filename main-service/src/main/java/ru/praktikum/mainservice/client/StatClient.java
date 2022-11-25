package ru.praktikum.mainservice.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.praktikum.mainservice.client.dto.EndpointHitDto;
import ru.praktikum.mainservice.event.mapper.EventMapper;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class StatClient extends BaseClient {

    //@Value("${ewm-stats-service.url}") String url,
    @Autowired
    public StatClient(RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory("http://localhost:9090"))
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

        post("/hit", endpointHitDto);
    }

    public ResponseEntity<Object> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, @Nullable Boolean unique) {

        Map<String, Object> parametrs = Map.of(
                "start", start.format(EventMapper.FORMATTER_EVENT_DATE),
                "end", end.format(EventMapper.FORMATTER_EVENT_DATE),
                "uri", uris,
                "unique", unique
        );

        log.info("uris={}", uris);
        return get("/stats/", parametrs);
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
