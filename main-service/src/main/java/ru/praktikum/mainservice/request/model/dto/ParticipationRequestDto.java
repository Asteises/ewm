package ru.praktikum.mainservice.request.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

/**
 * Заявка на участие в событии
 */

@Validated
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParticipationRequestDto {
    @JsonProperty("created")
    private String created;

    @JsonProperty("event")
    private Long event;

    @JsonProperty("id")
    private Long id;

    @JsonProperty("requester")
    private Long requester;

    @JsonProperty("status")
    private String status;
}
