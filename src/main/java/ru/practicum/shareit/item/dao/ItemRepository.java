package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    Item create(Item item);

    Item update(Item item);

    void deleteById(int itemId);

    Optional<Item> findItemById(int itemId);

    List<Item> findItemsByUser(int userId);

    List<Item> findItemsByParams(String text, boolean isOnlyAvailable);
}
