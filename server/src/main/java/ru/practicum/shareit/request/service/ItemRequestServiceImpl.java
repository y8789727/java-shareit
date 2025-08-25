package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemForRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoMapper;
import ru.practicum.shareit.request.dto.ItemRequestFullDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto createRequest(int requesterId, ItemRequestDto itemRequestDto) {
        User user = findUserById(requesterId);
        ItemRequest itemRequest = ItemRequestDtoMapper.fromDto(itemRequestDto);
        itemRequest.setRequester(user);
        itemRequest.setCreatedDate(Instant.now());

        return ItemRequestDtoMapper.toDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestFullDto> getUserRequests(int requesterId) {
        User user = findUserById(requesterId);

        List<ItemRequestFullDto> itemRequestsDto = itemRequestRepository.findByRequester(user.getId()).stream()
                .map(ItemRequestDtoMapper::toFullDto)
                .toList();

        final Map<Integer, List<ItemForRequestDto>> items = itemRepository.findByRequestsByUserId(user.getId()).stream()
                .map(ItemRequestDtoMapper::itemToDto)
                .collect(Collectors.groupingBy(ItemForRequestDto::getRequestId));

        itemRequestsDto.forEach(i -> i.setItems(items.getOrDefault(i.getId(), Collections.emptyList())));

        return itemRequestsDto;
    }

    @Override
    public List<ItemRequestDto> getOtherUsersRequests(int requesterId) {
        return itemRequestRepository.findByOtherUsers(requesterId).stream()
                .map(ItemRequestDtoMapper::toDto)
                .toList();
    }

    @Override
    public ItemRequestFullDto getRequestById(int requestId) {
        ItemRequestFullDto itemRequest = ItemRequestDtoMapper.toFullDto(itemRequestRepository.findById(requestId).orElseThrow(() -> new NoSuchElementException("Не найден запрос с id=" + requestId)));

        itemRequest.setItems(itemRepository.findByRequestId(itemRequest.getId()).stream()
                .map(ItemRequestDtoMapper::itemToDto)
                .toList());

        return itemRequest;
    }

    private User findUserById(int userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("Не найден пользователь с id=" + userId));
    }
}
