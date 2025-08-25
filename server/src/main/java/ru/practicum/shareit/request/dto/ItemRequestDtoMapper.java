package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class ItemRequestDtoMapper {

    public static ItemRequest fromDto(ItemRequestDto itemRequestDto) {
        return ItemRequest.builder()
                .id(itemRequestDto.getId())
                .description(itemRequestDto.getDescription())
                .build();
    }

    public static ItemRequestDto toDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .createdDate(LocalDateTime.ofInstant(itemRequest.getCreatedDate(), ZoneId.systemDefault()))
                .build();
    }

    public static ItemRequestFullDto toFullDto(ItemRequest itemRequest) {
        return ItemRequestFullDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .createdDate(LocalDateTime.ofInstant(itemRequest.getCreatedDate(), ZoneId.systemDefault()))
                .build();
    }

    public static ItemForRequestDto itemToDto(Item item) {
        return ItemForRequestDto.builder()
                .itemId(item.getId())
                .name(item.getName())
                .ownerId(item.getOwner().getId())
                .requestId(item.getRequest().getId())
                .build();
    }
}
