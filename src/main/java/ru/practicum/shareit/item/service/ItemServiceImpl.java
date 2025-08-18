package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemWithBookInfoDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    @Override
    public ItemDto create(int userId, ItemDto itemDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("Не найден пользователь с id=" + userId));

        if (itemDto.getName() == null
            || itemDto.getName().isBlank()
            || itemDto.getDescription() == null
            || itemDto.getDescription().isBlank()
            || itemDto.getAvailable() == null) {
            throw new ValidationException("Некорректные данные вещи");
        }

        Item itemToCreate = ItemDtoMapper.mapItemDtoToItem(itemDto);
        itemToCreate.setOwner(user);

        return ItemDtoMapper.mapItemToItemDto(itemRepository.save(itemToCreate));
    }

    @Override
    public ItemDto update(int userId, int itemId, ItemDto itemDto) {
        Item item = findItemByIdWithCheck(itemId);
        User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("Не найден пользователь с id=" + userId));

        if (item.getOwner().getId() != user.getId()) {
            throw new ValidationException("Пользователь id=" + user.getId() + " не может изменить вещь id=" + item.getId());
        }

        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            item.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        return ItemDtoMapper.mapItemToItemDto(findItemByIdWithCheck(itemRepository.save(item).getId()));
    }

    @Override
    public ItemDto findItemById(int itemId) {
        ItemDto itemDto = ItemDtoMapper.mapItemToItemDto(findItemByIdWithCheck(itemId));
        itemDto.setComments(commentRepository.findByItemId(itemId).stream()
                                                .map(ItemDtoMapper::mapCommentToCommentDto)
                                                .toList());
        return itemDto;
    }

    @Override
    public List<ItemWithBookInfoDto> findUserItems(int userId) {
        List<ItemWithBookInfoDto> itemsDto = itemRepository.findByUserWithBookInfo(userId);

        final Map<Integer, List<CommentDto>> comments = commentRepository.findByItemsOwner(userId).stream()
                .map(ItemDtoMapper::mapCommentToCommentDto)
                .collect(Collectors.groupingBy(CommentDto::getItemId));
        itemsDto.forEach(i -> i.setComments(comments.getOrDefault(i.getId(), Collections.emptyList())));

        return itemsDto;
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text.isBlank()) {
            return List.of();
        }
        return itemRepository.findByParams(text).stream()
                .map(ItemDtoMapper::mapItemToItemDto)
                .toList();
    }

    @Override
    public void deleteItemById(int itemId) {
        itemRepository.deleteById(itemId);
    }

    @Override
    public CommentDto createComment(int userId, int itemId, CommentDto comment) {
        Item item = findItemByIdWithCheck(itemId);
        User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("Не найден пользователь с id=" + userId));

        Instant moment = Instant.now();

        List<Booking> pastBookings = bookingRepository.findPastBookingsByBookerAndItem(userId, itemId, moment);
        if (pastBookings.isEmpty()) {
            throw new ValidationException("Пользователь userId=" + userId + " еще не использовал вещь id=" + itemId);
        }

        Comment createdComment = commentRepository.save(Comment.builder()
                        .text(comment.getText())
                        .item(item)
                        .author(user)
                        .created(moment)
                        .build());

        return ItemDtoMapper.mapCommentToCommentDto(createdComment);
    }

    private Item findItemByIdWithCheck(int itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> new NoSuchElementException("Не найдена вещь с id=" + itemId));
    }
}
