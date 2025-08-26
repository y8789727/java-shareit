package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.exception.ValidationException;

@Data
@Builder
public class ItemDto {
    private int id;
    private String name;
    private String description;
    private Boolean available;
    private Integer requestId;

    public void validateCreate() {
        if (getName() == null
            || getName().isBlank()
            || getDescription() == null
            || getDescription().isBlank()
            || getAvailable() == null) {
            throw new ValidationException("Некорректный запрос на создание");
        }
    }

    public void validateUpdate() {
        if ((getName() == null || getName().isBlank())
            && (getDescription() == null || getDescription().isBlank())
            && getAvailable() == null) {
            throw new ValidationException("Некорректный запрос на изменение");
        }
    }

}
