package ru.praktikum.mainservice.event.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class EventPublicFilterDto {

    private String state;
    private String text;
    private Long[] categories;
    private Boolean paid;
    private String rangeStart;
    private String rangeEnd;
    private Boolean onlyAvailable;
    private String sort;
}
