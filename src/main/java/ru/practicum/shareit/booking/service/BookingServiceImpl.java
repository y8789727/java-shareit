package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingStateParam;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    private Booking findBookingById(int bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(() -> new NoSuchElementException("Не найдено бронирование с id=" + bookingId));
    }

    @Override
    public Booking create(Booking booking, int bookerId, int itemId) {
        StringBuilder sb = new StringBuilder();
        Instant now = Instant.now();
        if (booking.getStartDate().isBefore(now)) {
            sb.append("Дата начала брони не может быть раньше текущей даты");
        }
        if (booking.getEndDate().isBefore(now)) {
            sb.append("Дата окончания брони не может быть раньше текущей даты");
        }
        if (booking.getEndDate().isBefore(booking.getStartDate())
            || booking.getEndDate().equals(booking.getStartDate())) {
            sb.append("Некорректная дата окончания");
        }
        if (!sb.isEmpty()) {
            throw new ValidationException(sb.toString());
        }

        booking.setBooker(userRepository.findById(bookerId).orElseThrow(() -> new NoSuchElementException("Не найден пользователь с id=" + bookerId)));

        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NoSuchElementException("Не найдена вещь с id=" + itemId));
        if (!item.isAvailable()) {
            throw new ValidationException("Вещь не доступна для бронирования");
        }
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);

        return bookingRepository.save(booking);
    }

    @Override
    public Booking approve(int bookingId, int ownerId, boolean approve) {
        Booking booking = findBookingById(bookingId);

        if (booking.getItem().getOwner().getId() != ownerId) {
            throw new ValidationException("Только владелец вещи может одобрить бронирование");
        } else if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new ValidationException("Неверный статус бронирования, одобрение не может быть выполнено");
        }

        booking.setStatus(approve ? BookingStatus.APPROVED : BookingStatus.REJECTED);

        return bookingRepository.save(booking);
    }

    @Override
    public Booking getBookingById(int bookingId, int userId) {
        Booking booking = findBookingById(bookingId);

        if (userId != booking.getBooker().getId() && userId != booking.getItem().getOwner().getId()) {
            throw new ValidationException("Пользователь " + userId + " не может просматривать бронирование");
        }

        return booking;
    }

    @Override
    public List<Booking> getBookingByBooker(int bookerId, BookingStateParam state) {
        userRepository.findById(bookerId).orElseThrow(() -> new NoSuchElementException("Не найден пользователь с id=" + bookerId));
        return bookingRepository.findByBookerWithSpecifiedState(bookerId, state);
    }

    @Override
    public List<Booking> getBookingByOwner(int ownerId, BookingStateParam state) {
        userRepository.findById(ownerId).orElseThrow(() -> new NoSuchElementException("Не найден пользователь с id=" + ownerId));
        return bookingRepository.findByOwnerWithSpecifiedState(ownerId, state);
    }
}
