package ru.praktikum.mainservice.request.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

/**
 * Данные нового пользователя
 */

@Validated
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewUserRequest {
    @JsonProperty("email")
    private String email = null;

    @JsonProperty("name")
    private String name = null;
}
