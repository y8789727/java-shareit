package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto create(int userId, ItemDto itemDto) {
        User user = userRepository.findUserById(userId).orElseThrow(() -> new NoSuchElementException("Не найден пользователь с id=" + userId));

        if (itemDto.getName() == null
            || itemDto.getName().isBlank()
            || itemDto.getDescription() == null
            || itemDto.getDescription().isBlank()
            || itemDto.getAvailable() == null) {
            throw new ValidationException("Некорректные данные вещи");
        }

        Item itemToCreate = ItemDtoMapper.mapItemDtoToItem(itemDto);
        itemToCreate.setOwnerId(user.getId());

        return ItemDtoMapper.mapItemToItemDto(itemRepository.create(itemToCreate));
    }

    @Override
    public ItemDto update(int userId, int itemId, ItemDto itemDto) {
        Item item = itemRepository.findItemById(itemId).orElseThrow(() -> new NoSuchElementException("Не найдена вещь с id=" + itemId));
        User user = userRepository.findUserById(userId).orElseThrow(() -> new NoSuchElementException("Не найден пользователь с id=" + userId));

        if (item.getOwnerId() != user.getId()) {
            throw new ValidationException("Пользователь id=" + user.getId() + " не может изменить вещь id=" + item.getId());
        }

        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            item.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        return ItemDtoMapper.mapItemToItemDto(itemRepository.update(item));
    }

    @Override
    public ItemDto findItemById(int itemId) {
        return ItemDtoMapper.mapItemToItemDto(itemRepository.findItemById(itemId).orElseThrow(() -> new NoSuchElementException("Не найдена вещь с id=" + itemId)));
    }

    @Override
    public List<ItemDto> findUserItems(int userId) {
        return itemRepository.findItemsByUser(userId).stream()
                .map(ItemDtoMapper::mapItemToItemDto)
                .toList();
    }

    @Override
    public List<ItemDto> searchItems(String text, boolean isOnlyAvailable) {
        return itemRepository.findItemsByParams(text, isOnlyAvailable).stream()
                .map(ItemDtoMapper::mapItemToItemDto)
                .toList();
    }

    @Override
    public void deleteItemById(int itemId) {
        itemRepository.deleteById(itemId);
    }
}
