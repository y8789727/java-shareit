package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Item {
    private int id;
    private String name;
    private String description;
    private boolean available;
    private int ownerId;
}
