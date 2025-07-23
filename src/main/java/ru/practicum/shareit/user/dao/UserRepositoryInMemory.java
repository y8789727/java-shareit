package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserRepositoryInMemory implements UserRepository {
    private final Map<Integer, User> userById = new HashMap<>();
    private final Map<String, User> userByEmail = new HashMap<>();
    private int lastId = 0;

    @Override
    public List<User> findAllUsers() {
        return userById.values().stream()
                .sorted(Comparator.comparingInt(User::getId))
                .toList();
    }

    @Override
    public Optional<User> findUserById(final int userId) {
        return Optional.ofNullable(userById.get(userId));
    }

    @Override
    public Optional<User> findUserByEmail(final String email) {
        return Optional.ofNullable(userByEmail.get(email));
    }

    @Override
    public User create(final User user) {
        user.setId(getNextId());
        userById.put(user.getId(), user);
        userByEmail.put(user.getEmail(), user);
        return user;
    }

    @Override
    public User update(final User user) {
        User oldUser = userById.get(user.getId());
        userById.put(user.getId(), user);

        if (!user.getEmail().equals(oldUser.getEmail())) {
            userByEmail.remove(oldUser.getEmail());
        }
        userByEmail.put(user.getEmail(), user);
        return user;
    }

    @Override
    public void deleteById(final int userId) {
        if (userById.containsKey(userId)) {
            User u = userById.get(userId);
            userByEmail.remove(u.getEmail());
            userById.remove(u.getId());
        }
    }

    private int getNextId() {
        return ++lastId;
    }
}
