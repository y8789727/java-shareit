package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.booking.dto.BookingStateParam;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto create(@RequestHeader("X-Sharer-User-Id") int bookerId,
                             @RequestBody BookingDto bookingDto) {
        return BookingDtoMapper.mapToDto(bookingService.create(BookingDtoMapper.mapFromDto(bookingDto), bookerId, bookingDto.getItemId()));
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approve(@RequestHeader("X-Sharer-User-Id") int ownerId,
                              @PathVariable int bookingId,
                              @RequestParam boolean approved) {
        return BookingDtoMapper.mapToDto(bookingService.approve(bookingId, ownerId, approved));
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@RequestHeader("X-Sharer-User-Id") int userId,
                                     @PathVariable int bookingId) {
        return BookingDtoMapper.mapToDto(bookingService.getBookingById(bookingId, userId));
    }

    @GetMapping
    public List<BookingDto> getBookerBookings(@RequestHeader("X-Sharer-User-Id") int bookerId,
                                              @RequestParam(required = false, defaultValue = "ALL") BookingStateParam state) {
        return bookingService.getBookingByBooker(bookerId, state).stream()
                .map(BookingDtoMapper::mapToDto)
                .toList();
    }

    @GetMapping("/owner")
    public List<BookingDto> getOwnerBookings(@RequestHeader("X-Sharer-User-Id") int ownerId,
                                             @RequestParam(required = false, defaultValue = "ALL") BookingStateParam state) {
        return bookingService.getBookingByOwner(ownerId, state).stream()
                .map(BookingDtoMapper::mapToDto)
                .toList();
    }

}
