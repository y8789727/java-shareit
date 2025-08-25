package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.user.dto.UserDtoMapper;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class BookingDtoMapper {
    public static BookingDto mapToDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .itemId(booking.getItem().getId())
                .start(LocalDateTime.ofInstant(booking.getStartDate(), ZoneId.systemDefault()))
                .end(LocalDateTime.ofInstant(booking.getEndDate(), ZoneId.systemDefault()))
                .status(booking.getStatus())
                .booker(UserDtoMapper.mapUserToUserDto(booking.getBooker()))
                .item(ItemDtoMapper.mapItemToItemDto(booking.getItem()))
                .build();
    }

    public static Booking mapFromDto(BookingDto bookingDto) {
        return Booking.builder()
                .id(bookingDto.getId())
                .startDate(bookingDto.getStart().atZone(ZoneId.systemDefault()).toInstant())
                .endDate(bookingDto.getEnd().atZone(ZoneId.systemDefault()).toInstant())
                .build();
    }
}
