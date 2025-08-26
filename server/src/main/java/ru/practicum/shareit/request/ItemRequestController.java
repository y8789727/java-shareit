package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestFullDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto createRequest(@RequestHeader("X-Sharer-User-Id") int requesterId,
                                        @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.createRequest(requesterId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestFullDto> getUserRequests(@RequestHeader("X-Sharer-User-Id") int requesterId) {
        return itemRequestService.getUserRequests(requesterId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getOtherUsersRequests(@RequestHeader("X-Sharer-User-Id") int requesterId) {
        return itemRequestService.getOtherUsersRequests(requesterId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestFullDto getRequestById(@PathVariable("requestId") int requestId) {
        return itemRequestService.getRequestById(requestId);
    }
}
