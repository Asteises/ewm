package ru.praktikum.statsservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * EndpointHit
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Entity
@Table(name = "endpoint_hit")
public class EndpointHit {

    @JsonProperty("id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @JsonProperty("app")
    private String app;

    @JsonProperty("uri")
    private String uri;

    @JsonProperty("ip")
    private String ip;

    @JsonProperty("created")
    private LocalDateTime created;
}
