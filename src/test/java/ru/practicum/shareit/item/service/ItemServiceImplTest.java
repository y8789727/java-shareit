package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dao.ItemRepositoryInMemory;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dao.UserRepositoryInMemory;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ItemServiceImplTest {
    private final UserRepository userRepository = new UserRepositoryInMemory();
    private final ItemRepository itemRepository = new ItemRepositoryInMemory();
    private final UserService userService = new UserServiceImpl(userRepository, itemRepository);
    private final ItemService itemService = new ItemServiceImpl(itemRepository, userRepository);
    private final UserDto user1 = userService.create(UserDto.builder()
                                                        .name("user1")
                                                        .email("user1@mail.mail")
                                                        .build());
    private final UserDto user2 = userService.create(UserDto.builder()
            .name("user2")
            .email("user2@mail.mail")
            .build());

    @Test
    public void checkCreateItem() {
        ItemDto item = ItemDto.builder()
                .name("test")
                .description("descr")
                .available(true)
                .build();

        ItemDto itemCreated = itemService.create(user1.getId(), item);

        assertThat(itemCreated)
                .hasFieldOrPropertyWithValue("name", "test")
                .hasFieldOrPropertyWithValue("description", "descr")
                .hasFieldOrPropertyWithValue("available", true);
        assertThat(itemCreated.getId()).isGreaterThan(0);
    }

    @Test
    public void checkUpdateItem() {
        ItemDto item = itemService.create(user1.getId(), ItemDto.builder()
                .name("test")
                .description("descr")
                .available(true)
                .build());

        item.setDescription("new descr");

        assertThat(itemService.update(user1.getId(), item.getId(), item))
                .hasFieldOrPropertyWithValue("description", "new descr");
    }

    @Test
    public void checkDeleteItem() {
        ItemDto item = itemService.create(user1.getId(), ItemDto.builder()
                .name("test")
                .description("descr")
                .available(true)
                .build());

        assertThat(itemService.findItemById(item.getId())).isNotNull();

        itemService.deleteItemById(item.getId());

        assertThatThrownBy(() -> itemService.findItemById(item.getId())).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    public void checkFindItemsByUser() {
        itemService.create(user2.getId(), ItemDto.builder()
                .name("test")
                .description("descr")
                .available(true)
                .build());

        itemService.create(user2.getId(),ItemDto.builder()
                .name("test2")
                .description("descr")
                .available(true)
                .build());

        assertThat(itemService.findUserItems(user2.getId())).size().isEqualTo(2);
    }

    @Test
    public void checkSearchItems() {
        itemService.create(user2.getId(), ItemDto.builder()
                .name("testSearch1")
                .description("descr")
                .available(true)
                .build());

        itemService.create(user2.getId(), ItemDto.builder()
                .name("testSearch2")
                .description("descr")
                .available(true)
                .build());

        assertThat(itemService.searchItems("search", true)).size().isEqualTo(2);
    }

    @Test
    public void whenItemInvalidThanExceptionThrown() {
        final ItemDto itemNoName = ItemDto.builder()
                .description("descr")
                .available(true)
                .build();

        assertThatThrownBy(() -> itemService.create(user1.getId(), itemNoName)).isInstanceOf(ValidationException.class);

        final ItemDto itemNoDescr = ItemDto.builder()
                .name("name")
                .available(true)
                .build();

        assertThatThrownBy(() -> itemService.create(user1.getId(), itemNoDescr)).isInstanceOf(ValidationException.class);

        final ItemDto itemNoAvailable = ItemDto.builder()
                .name("name")
                .description("descr")
                .build();

        assertThatThrownBy(() -> itemService.create(user1.getId(), itemNoAvailable)).isInstanceOf(ValidationException.class);
    }

    @Test
    public void whenWrongUserOnUpdateThenExceptionThrown() {
        ItemDto item = itemService.create(user1.getId(), ItemDto.builder()
                .name("check users")
                .description("descr")
                .available(false)
                .build());

        ItemDto itemUpdate = ItemDto.builder()
                .name("new name check users")
                .build();

        assertThatThrownBy(() -> itemService.update(user2.getId(), item.getId(), itemUpdate)).isInstanceOf(ValidationException.class);
    }
}