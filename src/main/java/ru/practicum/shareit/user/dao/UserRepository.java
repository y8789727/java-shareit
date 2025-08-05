package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    List<User> findAllUsers();

    Optional<User> findUserById(int userId);

    Optional<User> findUserByEmail(String email);

    User create(User user);

    User update(User user);

    void deleteById(int userId);
}
