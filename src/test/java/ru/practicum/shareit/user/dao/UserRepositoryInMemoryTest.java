package ru.practicum.shareit.user.dao;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.Assertions.assertThat;

class UserRepositoryInMemoryTest {
    private final UserRepository userRepository = new UserRepositoryInMemory();

    @Test
    public void checkCreateUser() {
        User user = User.builder()
                .name("test")
                .email("test1@test.test")
                .build();

        User userCreated = userRepository.create(user);
        assertThat(userCreated)
                .hasFieldOrPropertyWithValue("name", "test")
                .hasFieldOrPropertyWithValue("email", "test1@test.test");
        assertThat(userCreated.getId()).isGreaterThan(0);
    }

    @Test
    public void checkUpdateUser() {
        User user = userRepository.create(User.builder()
                .name("test")
                .email("test2@test.test")
                .build());

        User userUpdate = User.builder()
                .id(user.getId())
                .name(user.getName())
                .email("new@email.com")
                .build();

        assertThat(userRepository.update(userUpdate))
                .hasFieldOrPropertyWithValue("email", "new@email.com");

        assertThat(userRepository.findUserByEmail("test2@test.test")).isEmpty();
    }

    @Test
    public void checkDeleteUser() {
        User user = userRepository.create(User.builder()
                .name("test")
                .email("test3@test.test")
                .build());

        assertThat(userRepository.findUserById(user.getId())).isPresent();

        userRepository.deleteById(user.getId());

        assertThat(userRepository.findUserById(user.getId())).isEmpty();
    }

    @Test
    public void checkFindAllUsers() {
        userRepository.create(User.builder()
                .name("test")
                .email("test4@test.test")
                .build());

        userRepository.create(User.builder()
                .name("test")
                .email("test5@test.test")
                .build());

        assertThat(userRepository.findAllUsers()).size().isGreaterThan(1);
    }

    @Test
    public void checkFindUserByEmail() {
        User user = userRepository.create(User.builder()
                .name("test")
                .email("test6@test.test")
                .build());

        assertThat(userRepository.findUserByEmail(user.getEmail())).isPresent();
    }

}