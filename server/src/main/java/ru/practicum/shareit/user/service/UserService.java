package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();

    UserDto getUserById(int userId);

    UserDto create(UserDto userDto);

    UserDto update(int userId, UserDto userDto);

    void delete(int userId);
}
