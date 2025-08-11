package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ItemDto {
    private int id;
    private String name;
    private String description;
    private Boolean available;
    private List<CommentDto> comments;
    private ItemBookInfo lastBooking;
    private ItemBookInfo nextBooking;
}
