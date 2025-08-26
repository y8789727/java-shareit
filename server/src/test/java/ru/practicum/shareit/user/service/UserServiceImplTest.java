package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import ru.practicum.shareit.exception.UserEmailConfilct;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@ComponentScan(basePackages = "ru.practicum.shareit")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplTest {

    private final UserServiceImpl userService;
    private final ItemService itemService;

    @Test
    public void checkCreateUser() {
        UserDto user = UserDto.builder()
                .name("test")
                .email("test1@test.test")
                .build();

        UserDto userCreated = userService.create(user);
        assertThat(userCreated)
                .hasFieldOrPropertyWithValue("name", "test")
                .hasFieldOrPropertyWithValue("email", "test1@test.test");
        assertThat(userCreated.getId()).isGreaterThan(0);
    }

    @Test
    public void checkCreateUserDuplicateEmail() {
        UserDto user = UserDto.builder()
                .name("test")
                .email("test1@test.test")
                .build();

        userService.create(user);

        UserDto user2 = UserDto.builder()
                .name("test new")
                .email("test1@test.test")
                .build();

        assertThatThrownBy(() -> userService.create(user2)).isInstanceOf(UserEmailConfilct.class);
    }

    @Test
    public void checkUpdateUser() {
        UserDto user = userService.create(UserDto.builder()
                .name("test")
                .email("test2@test.test")
                .build());

        UserDto userUpdateEmail = UserDto.builder()
                        .email("new@email.com")
                        .build();

        assertThat(userService.update(user.getId(), userUpdateEmail))
                .hasFieldOrPropertyWithValue("email", "new@email.com");

        UserDto userUpdateName = UserDto.builder()
                .name("new name")
                .build();

        assertThat(userService.update(user.getId(), userUpdateName))
                .hasFieldOrPropertyWithValue("name", "new name");
    }

    @Test
    public void checkUpdateUserDuplicateEmail() {
        UserDto user1 = userService.create(UserDto.builder()
                .name("test")
                .email("test50@test.test")
                .build());

        UserDto user2 = userService.create(UserDto.builder()
                .name("test")
                .email("test51@test.test")
                .build());

        UserDto user2UpdateEmail = UserDto.builder()
                .email("test50@test.test")
                .build();

        assertThatThrownBy(() -> userService.update(user2.getId(), user2UpdateEmail)).isInstanceOf(UserEmailConfilct.class);
    }

    @Test
    public void checkDeleteUser() {
        UserDto user = userService.create(UserDto.builder()
                .name("test")
                .email("test3@test.test")
                .build());

        assertThat(userService.getUserById(user.getId())).isNotNull();

        userService.delete(user.getId());

        assertThatThrownBy(() -> userService.getUserById(user.getId())).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    public void checkFindAllUsers() {
        userService.create(UserDto.builder()
                .name("test")
                .email("test4@test.test")
                .build());

        userService.create(UserDto.builder()
                .name("test")
                .email("test5@test.test")
                .build());

        assertThat(userService.getAllUsers()).size().isGreaterThan(1);
    }

    @Test
    public void whenUserItemExistsThenFailToDelete() {
        UserDto user = UserDto.builder()
                .name("test")
                .email("testItem@test.test")
                .build();

        final UserDto userCreated = userService.create(user);

        ItemDto item = ItemDto.builder()
                .name("test")
                .description("descr")
                .available(true)
                .build();

        ItemDto itemCreated = itemService.create(userCreated.getId(), item);

        assertThatThrownBy(() -> userService.delete(userCreated.getId())).isInstanceOf(ValidationException.class);
    }
 }