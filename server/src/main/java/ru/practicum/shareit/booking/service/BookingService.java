package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingStateParam;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {
    Booking create(Booking booking, int bookerId, int itemId);

    Booking approve(int bookingId, int ownerId, boolean approve);

    Booking getBookingById(int bookingId, int userId);

    List<Booking> getBookingByBooker(int bookerId, BookingStateParam state);

    List<Booking> getBookingByOwner(int ownerId, BookingStateParam state);
}
