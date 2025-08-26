package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestFullDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createRequest(int requesterId, ItemRequestDto itemRequestDto);

    List<ItemRequestFullDto> getUserRequests(int requesterId);

    List<ItemRequestDto> getOtherUsersRequests(int requesterId);

    ItemRequestFullDto getRequestById(int requestId);
}
