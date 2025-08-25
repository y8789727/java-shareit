package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;

import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingService bookingService;

    @Test
    public void testCreateBooking() throws Exception {
        BookingDto bookingDtoCreate = BookingDto.builder()
                .itemId(1)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now())
                .build();
        User itemOwner = User.builder()
                .id(1)
                .name("Owner")
                .email("email@email.com")
                .build();
        Item item = Item.builder()
                .id(1)
                .name("item name")
                .description("desc")
                .isAvailable(true)
                .owner(itemOwner)
                .build();
        User itemBooker = User.builder()
                .id(2)
                .name("Booker")
                .email("email2@email.com")
                .build();
        Booking bookingCreated = Booking.builder()
                .id(1)
                .startDate(Instant.now())
                .endDate(Instant.now())
                .status(BookingStatus.WAITING)
                .item(item)
                .booker(itemBooker)
                .build();
        when(bookingService.create(any(), anyInt(), anyInt()))
                .thenReturn(bookingCreated);

        mvc.perform(post("/bookings")
                    .content(mapper.writeValueAsString(bookingDtoCreate))
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("X-Sharer-User-Id", itemBooker.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingCreated.getId()), Integer.class))
                .andExpect(jsonPath("$.status", is(BookingStatus.WAITING.toString()), String.class))
                .andExpect(jsonPath("$.booker.id", is(itemBooker.getId()), Integer.class))
                .andExpect(jsonPath("$.item.id", is(item.getId()), Integer.class));
    }

    @Test
    public void testApproveBooking() throws Exception {
        User itemOwner = User.builder()
                .id(1)
                .name("Owner")
                .email("email@email.com")
                .build();
        Item item = Item.builder()
                .id(1)
                .name("item name")
                .description("desc")
                .isAvailable(true)
                .owner(itemOwner)
                .build();
        User itemBooker = User.builder()
                .id(2)
                .name("Booker")
                .email("email2@email.com")
                .build();
        Booking bookingCreated = Booking.builder()
                .id(1)
                .startDate(Instant.now())
                .endDate(Instant.now())
                .status(BookingStatus.APPROVED)
                .item(item)
                .booker(itemBooker)
                .build();
        when(bookingService.approve(anyInt(), anyInt(), anyBoolean()))
                .thenReturn(bookingCreated);

        mvc.perform(patch("/bookings/" + bookingCreated.getId() + "?approved=true")
                            .characterEncoding(StandardCharsets.UTF_8)
                            .accept(MediaType.APPLICATION_JSON)
                            .header("X-Sharer-User-Id", itemBooker.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingCreated.getId()), Integer.class))
                .andExpect(jsonPath("$.status", is(BookingStatus.APPROVED.toString()), String.class))
                .andExpect(jsonPath("$.booker.id", is(itemBooker.getId()), Integer.class))
                .andExpect(jsonPath("$.item.id", is(item.getId()), Integer.class));
    }
}