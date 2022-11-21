package ru.praktikum.mainservice.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.praktikum.mainservice.client.dto.EndpointHitDto;
import ru.praktikum.mainservice.event.mapper.EventMapper;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

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

        post("/stats/hit", endpointHitDto);
    }

    public Long getViews(long eventId) {

        return (Long) get("/stats/" + eventId).getBody();
    }
}
