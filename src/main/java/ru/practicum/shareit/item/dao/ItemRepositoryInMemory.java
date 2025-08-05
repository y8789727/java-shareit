package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class ItemRepositoryInMemory implements ItemRepository {
    private final Map<Integer, Item> itemsById = new HashMap<>();
    private final Map<Integer, List<Item>> itemsByUserId = new HashMap<>();
    private int lastId = 0;

    @Override
    public Item create(Item item) {
        item.setId(getNextId());

        itemsById.put(item.getId(), item);

        if (!itemsByUserId.containsKey(item.getOwnerId())) {
            itemsByUserId.put(item.getOwnerId(), new ArrayList<>());
        }
        itemsByUserId.get(item.getOwnerId()).add(item);

        return item;
    }

    @Override
    public Item update(final Item item) {
        itemsById.put(item.getId(), item);
        itemsByUserId.get(item.getOwnerId()).removeIf(i -> i.getId() == item.getId());
        itemsByUserId.get(item.getOwnerId()).add(item);
        return item;
    }

    @Override
    public void deleteById(final int itemId) {
        if (itemsById.containsKey(itemId)) {
            Item item = itemsById.get(itemId);
            itemsByUserId.get(item.getOwnerId()).removeIf(i -> i.getId() == itemId);
            itemsById.remove(itemId);
        }
    }

    @Override
    public Optional<Item> findItemById(int itemId) {
        return Optional.ofNullable(itemsById.get(itemId));
    }

    @Override
    public List<Item> findItemsByUser(int userId) {
        return itemsByUserId.get(userId) != null ? itemsByUserId.get(userId) : List.of();
    }

    @Override
    public List<Item> findItemsByParams(final String text, final boolean isOnlyAvailable) {
        if (text.isBlank()) {
            return List.of();
        }

        return itemsById.values().stream()
                .filter(i -> (i.getName().toLowerCase().contains(text.toLowerCase())
                                   || i.getDescription().toLowerCase().contains(text.toLowerCase()))
                                  && (!isOnlyAvailable || i.isAvailable()))
                .toList();
    }

    private int getNextId() {
        return ++lastId;
    }
}
