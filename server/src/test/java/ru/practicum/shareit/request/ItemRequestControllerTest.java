package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestFullDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemRequestService itemRequestService;

    @Test
    public void testCreateRequest() throws Exception {
        ItemRequestDto itemRequestDtoToCreate = ItemRequestDto.builder()
                .description("descr")
                .build();
        ItemRequestDto itemRequestDtoCreated = ItemRequestDto.builder()
                .description("descr")
                .id(1)
                .createdDate(LocalDateTime.now())
                .build();
        when(itemRequestService.createRequest(anyInt(), any()))
                .thenReturn(itemRequestDtoCreated);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDtoToCreate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDtoCreated.getId()), Integer.class));
    }

    @Test
    public void testGetUserRequests() throws Exception {
        ItemRequestFullDto itemRequest1 = ItemRequestFullDto.builder()
                .description("descr1")
                .id(1)
                .createdDate(LocalDateTime.now())
                .build();
        ItemRequestFullDto itemRequest2 = ItemRequestFullDto.builder()
                .description("descr2")
                .id(2)
                .createdDate(LocalDateTime.now())
                .build();
        when(itemRequestService.getUserRequests(anyInt()))
                .thenReturn(List.of(itemRequest1, itemRequest2));

        mvc.perform(get("/requests")
                    .characterEncoding(StandardCharsets.UTF_8)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(1), Integer.class))
                .andExpect(jsonPath("$.[1].id", is(2), Integer.class));
    }

    @Test
    public void testGetOtherUserRequests() throws Exception {
        ItemRequestDto itemRequest1 = ItemRequestDto.builder()
                .description("descr1")
                .id(1)
                .createdDate(LocalDateTime.now())
                .build();
        ItemRequestDto itemRequest2 = ItemRequestDto.builder()
                .description("descr2")
                .id(2)
                .createdDate(LocalDateTime.now())
                .build();
        when(itemRequestService.getOtherUsersRequests(anyInt()))
                .thenReturn(List.of(itemRequest1, itemRequest2));

        mvc.perform(get("/requests/all")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(1), Integer.class))
                .andExpect(jsonPath("$.[1].id", is(2), Integer.class));
    }

    @Test
    public void testGetRequestById() throws Exception {
        ItemRequestFullDto itemRequest = ItemRequestFullDto.builder()
                .description("descr1")
                .id(1)
                .createdDate(LocalDateTime.now())
                .build();
        when(itemRequestService.getRequestById(anyInt()))
                .thenReturn(itemRequest);

        mvc.perform(get("/requests/" + itemRequest.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1), Integer.class));
    }

    @Test
    public void testRequestNotExists() throws Exception {
        when(itemRequestService.getRequestById(anyInt()))
                .thenThrow(new NoSuchElementException("msg"));

        mvc.perform(get("/requests/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2))
                .andExpect(status().is(404));
    }

}