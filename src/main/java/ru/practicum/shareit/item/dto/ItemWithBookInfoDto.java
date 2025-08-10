package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ItemWithBookInfoDto {
    private int id;
    private String name;
    private String description;
    private Boolean available;
    private ItemBookInfo lastBooking;
    private ItemBookInfo nextBooking;
    private List<CommentDto> comments;

    public ItemWithBookInfoDto(int id, String name, String description, Boolean available,
                               Instant prevBookStartDt, Instant prevBookEndDt,
                               Instant nextBookStartDt, Instant nextBookEndDt) {
        this(id, name, description, available,
            new ItemBookInfo(prevBookStartDt, prevBookEndDt),
            new ItemBookInfo(nextBookStartDt, nextBookEndDt), List.of());
    }
}
