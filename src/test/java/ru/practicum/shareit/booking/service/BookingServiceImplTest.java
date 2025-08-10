package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import ru.practicum.shareit.booking.dto.BookingStateParam;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

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
class BookingServiceImplTest {
    private final BookingServiceImpl bookingService;
    private final ItemService itemService;
    private final UserRepository userRepository;
    private final UserService userService;
    private UserDto user1;
    private UserDto user2;
    private ItemDto item1;

    private final static String user1Email = "user1@mail.mail";
    private final static String user2Email = "user2@mail.mail";

    private final static String itemName1 = "item 1";

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

    private ItemDto getItemDtoByName(String name, boolean available) {
        ItemDto itemDto;
        List<ItemDto> itemDtoList = itemService.searchItems(name);
        if (!itemDtoList.isEmpty()) {
            itemDto = itemDtoList.getFirst();
        } else {
            itemDto = itemService.create(user1.getId(),
                                         ItemDto.builder()
                                         .name(name)
                                         .description(name)
                                         .available(available)
                                         .build());
        }
        return itemDto;
    }

    @BeforeEach
    public void beforeEach() {
        user1 = getUserDtoByEmail(user1Email);
        user2 = getUserDtoByEmail(user2Email);
        item1 = getItemDtoByName(itemName1, true);
    }

    @Test
    public void testCreateAndApproveBooking() {
        Booking booking = bookingService.create(Booking.builder()
                .startDate(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant())
                .endDate(LocalDate.now().plusDays(3).atStartOfDay(ZoneId.systemDefault()).toInstant())
                .build(), user2.getId(), item1.getId());

        assertThat(booking)
                .hasFieldOrProperty("id")
                .hasFieldOrPropertyWithValue("status", BookingStatus.WAITING);

        assertThat(booking.getItem())
                .hasFieldOrPropertyWithValue("id", item1.getId());

        assertThat(booking.getBooker())
                .hasFieldOrPropertyWithValue("id", user2.getId());

        booking = bookingService.approve(booking.getId(), user1.getId(), true);

        assertThat(booking)
                .hasFieldOrPropertyWithValue("status", BookingStatus.APPROVED);

        // check double approve cause exception
        final Booking b = booking;
        assertThatThrownBy(() -> bookingService.approve(b.getId(), user1.getId(), true))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    public void testGetBookingById() {
        Booking booking = bookingService.create(Booking.builder()
                .startDate(LocalDate.now().plusDays(4).atStartOfDay(ZoneId.systemDefault()).toInstant())
                .endDate(LocalDate.now().plusDays(5).atStartOfDay(ZoneId.systemDefault()).toInstant())
                .build(), user2.getId(), item1.getId());

        assertThat(bookingService.getBookingById(booking.getId(), user2.getId()))
                .hasFieldOrPropertyWithValue("id", booking.getId());
    }

    @Test
    public void testGetBookingsByOwner() {
        ItemDto itemDto = getItemDtoByName("testBookingByOwner", true);

        Booking booking = bookingService.create(Booking.builder()
                .startDate(LocalDate.now().plusDays(6).atStartOfDay(ZoneId.systemDefault()).toInstant())
                .endDate(LocalDate.now().plusDays(7).atStartOfDay(ZoneId.systemDefault()).toInstant())
                .build(), user2.getId(), itemDto.getId());

        bookingService.approve(booking.getId(), user1.getId(), false);

        List<Booking> rejectedBooking = bookingService.getBookingByOwner(user1.getId(), BookingStateParam.REJECTED);

        assertThat(rejectedBooking).hasSize(1);
    }

    @Test
    public void testGetBookingsByBooker() {
        ItemDto itemDto = getItemDtoByName("testBookingByBooker", true);

        bookingService.create(Booking.builder()
                .startDate(LocalDate.now().plusDays(6).atStartOfDay(ZoneId.systemDefault()).toInstant())
                .endDate(LocalDate.now().plusDays(7).atStartOfDay(ZoneId.systemDefault()).toInstant())
                .build(), user2.getId(), itemDto.getId());

        List<Booking> rejectedBooking = bookingService.getBookingByBooker(user2.getId(), BookingStateParam.ALL);

        assertThat(rejectedBooking).hasSizeGreaterThanOrEqualTo(1);
    }

    @Test
    public void testBookingWithBadDates() {
        assertThatThrownBy(() -> bookingService.create(Booking.builder()
                                    .startDate(LocalDate.now().minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant())
                                    .endDate(LocalDate.now().minusDays(2).atStartOfDay(ZoneId.systemDefault()).toInstant())
                                    .build(), user2.getId(), item1.getId()))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    public void testBookingUnavailableItem() {
        ItemDto itemDto = getItemDtoByName("unavailableItem", false);

        assertThatThrownBy(() -> bookingService.create(Booking.builder()
                .startDate(LocalDate.now().plusDays(10).atStartOfDay(ZoneId.systemDefault()).toInstant())
                .endDate(LocalDate.now().plusDays(11).atStartOfDay(ZoneId.systemDefault()).toInstant())
                .build(), user2.getId(), itemDto.getId()))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    public void testBookingNonExistentItem() {
        assertThatThrownBy(() -> bookingService.create(Booking.builder()
                .startDate(LocalDate.now().plusDays(8).atStartOfDay(ZoneId.systemDefault()).toInstant())
                .endDate(LocalDate.now().plusDays(9).atStartOfDay(ZoneId.systemDefault()).toInstant())
                .build(), user2.getId(), -99))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    public void testBookingApproveOnlyByOwner() {
        ItemDto itemDto = getItemDtoByName("testBookingBadApprove", true);

        Booking booking = bookingService.create(Booking.builder()
                .startDate(LocalDate.now().plusDays(6).atStartOfDay(ZoneId.systemDefault()).toInstant())
                .endDate(LocalDate.now().plusDays(7).atStartOfDay(ZoneId.systemDefault()).toInstant())
                .build(), user2.getId(), itemDto.getId());

        assertThatThrownBy(() -> bookingService.approve(booking.getId(), user2.getId(), true))
                .isInstanceOf(ValidationException.class);
    }
}