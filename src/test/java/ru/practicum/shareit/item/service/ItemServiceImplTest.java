package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookInfoDto;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@ComponentScan(basePackages = "ru.practicum.shareit")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplTest {
    private final UserRepository userRepository;
    private final UserService userService;
    private final ItemService itemService;
    private final BookingService bookingService;
    private UserDto user1;
    private UserDto user2;

    private final static String user1Email = "user1@mail.mail";
    private final static String user2Email = "user2@mail.mail";

    private UserDto getUserDtoByEmail(String email) {
        UserDto userDto;
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            userDto = userService.getUserById(userOpt.get().getId());
        } else {
            userDto = userService.create(UserDto.builder()
                    .name(email)
                    .email(email)
                    .build());
        }
        return userDto;
    }

    @BeforeEach
    public void beforeEach() {
        user1 = getUserDtoByEmail(user1Email);
        user2 = getUserDtoByEmail(user2Email);
    }

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
        ItemDto itemForBooking = itemService.create(user2.getId(),ItemDto.builder()
                .name("test2")
                .description("descr")
                .available(true)
                .build());

        Instant bookingStartDate = LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
        bookingService.create(Booking.builder()
                .startDate(bookingStartDate)
                .endDate(LocalDate.now().plusDays(3).atStartOfDay(ZoneId.systemDefault()).toInstant())
                .build(), user1.getId(), itemForBooking.getId());

        List<ItemWithBookInfoDto> itemsList = itemService.findUserItems(user2.getId());

        assertThat(itemsList).size().isEqualTo(1);

        ItemWithBookInfoDto item = itemsList.getFirst();
        assertThat(item).hasFieldOrPropertyWithValue("id", itemForBooking.getId());
        assertThat(item.getNextBooking()).hasFieldOrPropertyWithValue("startDate", bookingStartDate);
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

        assertThat(itemService.searchItems("search")).size().isEqualTo(2);
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