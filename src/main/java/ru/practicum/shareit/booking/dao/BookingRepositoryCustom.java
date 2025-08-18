package ru.practicum.shareit.booking.dao;

import ru.practicum.shareit.booking.dto.BookingStateParam;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingRepositoryCustom {
    List<Booking> findByBookerWithSpecifiedState(int bookerId, BookingStateParam state);

    List<Booking> findByOwnerWithSpecifiedState(int ownerId, BookingStateParam state);
}
