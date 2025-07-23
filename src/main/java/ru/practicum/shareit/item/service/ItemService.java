package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import java.util.List;

public interface ItemService {

    ItemDto create(int userId, ItemDto itemDto);

    ItemDto update(int userId, int itemId, ItemDto itemDto);

    ItemDto findItemById(int itemId);

    List<ItemDto> findUserItems(int userId);

    List<ItemDto> searchItems(String text, boolean isOnlyAvailable);

    void deleteItemById(int itemId);
}
