package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ItemRequestFullDto {
    private int id;
    private String description;
    @JsonProperty("created")
    private LocalDateTime createdDate;
    private List<ItemForRequestDto> items;
}
