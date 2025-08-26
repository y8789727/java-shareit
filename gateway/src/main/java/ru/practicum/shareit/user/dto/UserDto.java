package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.exception.ValidationException;

@Data
@Builder
public class UserDto {
    private int id;
    private String name;
    @Email
    private String email;

    public void validateCreate() {
        if (getName() == null
            || getName().isBlank()
            || getEmail() == null
            || getEmail().isBlank()) {
            throw new ValidationException("Некорректные данные пользователя");
        }
    }

    public void validateUpdate() {
        if ((getName() == null || getName().isBlank())
            && (getEmail() == null || getEmail().isBlank())) {
            throw new ValidationException("Некорректные данные пользователя");
        }
    }
}
