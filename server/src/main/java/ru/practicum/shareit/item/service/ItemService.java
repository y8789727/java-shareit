package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemWithBookInfoDto;
import ru.practicum.shareit.item.dto.ItemDto;
import java.util.List;

public interface ItemService {

    ItemDto create(int userId, ItemDto itemDto);

    ItemDto update(int userId, int itemId, ItemDto itemDto);

    ItemDto findItemById(int itemId);

    List<ItemWithBookInfoDto> findUserItems(int userId);

    List<ItemDto> searchItems(String text);

    void deleteItemById(int itemId);

    CommentDto createComment(int userId, int itemId, CommentDto comment);
}
