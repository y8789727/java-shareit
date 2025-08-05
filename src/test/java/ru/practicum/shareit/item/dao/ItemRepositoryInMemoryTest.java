package ru.practicum.shareit.item.dao;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;

import static org.assertj.core.api.Assertions.assertThat;

class ItemRepositoryInMemoryTest {
    private final ItemRepository itemRepository = new ItemRepositoryInMemory();

    @Test
    public void checkCreateItem() {
        Item item = Item.builder()
                .name("test")
                .description("descr")
                .available(true)
                .build();

        Item itemCreated = itemRepository.create(item);

        assertThat(itemCreated)
                .hasFieldOrPropertyWithValue("name", "test")
                .hasFieldOrPropertyWithValue("description", "descr")
                .hasFieldOrPropertyWithValue("available", true);
        assertThat(itemCreated.getId()).isGreaterThan(0);
    }

    @Test
    public void checkUpdateItem() {
        Item item = itemRepository.create(Item.builder()
                .name("test")
                .description("descr")
                .available(true)
                .build());

        item.setDescription("aaa");

        assertThat(itemRepository.update(item))
                .hasFieldOrPropertyWithValue("description", "aaa");
    }

    @Test
    public void checkDeleteItem() {
        Item item = itemRepository.create(Item.builder()
                .name("test")
                .description("descr")
                .available(true)
                .build());

        assertThat(itemRepository.findItemById(item.getId())).isPresent();

        itemRepository.deleteById(item.getId());

        assertThat(itemRepository.findItemById(item.getId())).isEmpty();
    }

    @Test
    public void checkFindItemsByUser() {
        itemRepository.create(Item.builder()
                .name("test")
                .description("descr")
                .available(true)
                .ownerId(1)
                .build());

        itemRepository.create(Item.builder()
                .name("test2")
                .description("descr")
                .available(true)
                .ownerId(1)
                .build());

        assertThat(itemRepository.findItemsByUser(1)).size().isEqualTo(2);
    }

    @Test
    public void checkSearchItems() {
        itemRepository.create(Item.builder()
                .name("testSearch1")
                .description("descr")
                .available(true)
                .ownerId(2)
                .build());

        itemRepository.create(Item.builder()
                .name("testSearch2")
                .description("descr")
                .available(true)
                .ownerId(2)
                .build());

        assertThat(itemRepository.findItemsByParams("search", true)).size().isEqualTo(2);

        assertThat(itemRepository.findItemsByParams("", false)).size().isEqualTo(0);
    }
}