package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestFullDto;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ComponentScan(basePackages = "ru.practicum.shareit")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceImplTest {

    private final ItemRequestService itemRequestService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final ItemService itemService;

    private UserDto user1;
    private UserDto user2;

    private static final String user1Email = "user1@mail.mail";
    private static final String user2Email = "user2@mail.mail";

    private UserDto getUserDtoByEmail(String email) {
        UserDto userDto;
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            userDto = userService.getUserById(userOpt.get().getId());
        } else {
            userDto = userService.create(UserDto.builder()
                    .name(email)
                    .email(email)
                    .build());
        }
        return userDto;
    }

    @BeforeEach
    public void beforeEach() {
        user1 = getUserDtoByEmail(user1Email);
        user2 = getUserDtoByEmail(user2Email);
    }

    @Test
    public void checkCreateTest() {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .description("item req descr")
                .build();

        ItemRequestDto itemRequestCreated = itemRequestService.createRequest(user1.getId(), itemRequestDto);

        assertThat(itemRequestCreated)
                .hasFieldOrPropertyWithValue("description", "item req descr");
        assertThat(itemRequestCreated.getId()).isGreaterThan(0);
    }

    @Test
    public void whenItemForRequestThenItemInfoExists() {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .description("item req descr again")
                .build();

        ItemRequestDto itemRequestCreated = itemRequestService.createRequest(user1.getId(), itemRequestDto);

        ItemDto itemDto = ItemDto.builder()
                .name("item for request")
                .available(true)
                .description("descr")
                .requestId(itemRequestCreated.getId())
                .build();
        final ItemDto itemDtoCreated = itemService.create(user2.getId(), itemDto);

        ItemRequestFullDto itemRequestById = itemRequestService.getRequestById(itemRequestCreated.getId());

        assertThat(itemRequestById)
                .hasFieldOrPropertyWithValue("id", itemRequestCreated.getId())
                .hasFieldOrProperty("items");

        assertThat(itemRequestById.getItems()).size().isEqualTo(1);

        assertThat(itemRequestById.getItems().getFirst())
                .hasFieldOrPropertyWithValue("itemId", itemDtoCreated.getId());

        List<ItemRequestFullDto> itemsForRequester = itemRequestService.getUserRequests(user1.getId());
        assertThat(itemsForRequester).size().isGreaterThanOrEqualTo(1);

        List<ItemRequestFullDto> requestsWithItem =  itemsForRequester.stream()
                .filter(ir -> ir.getItems().getFirst().getItemId() == itemDtoCreated.getId())
                .toList();
        assertThat(requestsWithItem).size().isEqualTo(1);
    }

    @Test
    public void whenOtherUserSearchThenFound() {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .description("item req descr again and again")
                .build();

        ItemRequestDto itemRequestCreated = itemRequestService.createRequest(user1.getId(), itemRequestDto);

        List<ItemRequestDto> itemRequests = itemRequestService.getOtherUsersRequests(user2.getId());
        assertThat(itemRequests).size().isGreaterThanOrEqualTo(1);
    }

}