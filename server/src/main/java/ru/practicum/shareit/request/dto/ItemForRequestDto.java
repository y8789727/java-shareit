package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemForRequestDto {
    private int itemId;
    private String name;
    private int ownerId;
    @JsonIgnore
    private int requestId;
}
