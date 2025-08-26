package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookInfoDto;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.core.Is.is;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemService itemService;

    @Test
    public void testCreateItem() throws Exception {
        ItemDto itemToCreate = ItemDto.builder()
                .name("name")
                .description("desc")
                .available(true)
                .requestId(21)
                .build();
        ItemDto itemCreated = ItemDto.builder()
                .name("name")
                .description("desc")
                .available(true)
                .requestId(21)
                .id(1)
                .build();
        when(itemService.create(anyInt(), any()))
                .thenReturn(itemCreated);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemToCreate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 11))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemCreated.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(itemCreated.getName()), String.class))
                .andExpect(jsonPath("$.requestId", is(itemCreated.getRequestId()), Integer.class));
    }

    @Test
    public void testUpdateItem() throws Exception {
        ItemDto itemToUpdate = ItemDto.builder()
                .name("new name")
                .build();
        ItemDto itemUpdated = ItemDto.builder()
                .name("new name")
                .description("desc")
                .available(true)
                .requestId(21)
                .id(1)
                .build();
        when(itemService.update(anyInt(), anyInt(), any()))
                .thenReturn(itemUpdated);

        mvc.perform(patch("/items/" + itemUpdated.getId())
                        .content(mapper.writeValueAsString(itemToUpdate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 11))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemUpdated.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(itemUpdated.getName()), String.class))
                .andExpect(jsonPath("$.requestId", is(itemUpdated.getRequestId()), Integer.class));
    }

    @Test
    public void testDeleteItem() throws Exception {
        mvc.perform(delete("/items/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void testFindItemById() throws Exception {
        ItemDto item = ItemDto.builder()
                .name("new name")
                .description("desc")
                .available(true)
                .requestId(21)
                .id(1)
                .build();
        when(itemService.findItemById(anyInt()))
                .thenReturn(item);

        mvc.perform(get("/items/" + item.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(item.getName()), String.class))
                .andExpect(jsonPath("$.requestId", is(item.getRequestId()), Integer.class));
    }

    @Test
    public void testSearchItems() throws Exception {
        ItemDto item1 = ItemDto.builder()
                .name("name 1")
                .description("desc")
                .available(true)
                .requestId(21)
                .id(1)
                .build();
        ItemDto item2 = ItemDto.builder()
                .name("name 2")
                .description("desc")
                .available(false)
                .id(2)
                .build();
        when(itemService.searchItems(anyString()))
                .thenReturn(List.of(item1, item2));

        mvc.perform(get("/items/search?text=qqq")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(item1.getId()), Integer.class))
                .andExpect(jsonPath("$.[1].id", is(item2.getId()), Integer.class));
    }

    @Test
    public void testGetUserItems() throws Exception {
        ItemWithBookInfoDto item1 = new ItemWithBookInfoDto(
                1,
                "name1",
                "descr",
                true,
                Instant.now(),
                Instant.now(),
                Instant.now(),
                Instant.now());
        ItemWithBookInfoDto item2 = new ItemWithBookInfoDto(
                2,
                "name2",
                "descr",
                false,
                Instant.now(),
                Instant.now(),
                Instant.now(),
                Instant.now());
        when(itemService.findUserItems(anyInt()))
                .thenReturn(List.of(item1, item2));

        mvc.perform(get("/items")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 11))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(item1.getId()), Integer.class))
                .andExpect(jsonPath("$.[1].id", is(item2.getId()), Integer.class));
    }

    @Test
    public void testPostComment() throws Exception {
        CommentDto commentDtoToCreate = CommentDto.builder()
                .text("comment text")
                .build();
        CommentDto commentDtoCreated = CommentDto.builder()
                .text("comment text")
                .itemId(21)
                .created(LocalDateTime.now())
                .authorName("Author")
                .id(1)
                .build();
        when(itemService.createComment(anyInt(), anyInt(), any()))
                .thenReturn(commentDtoCreated);

        mvc.perform(post("/items/" + commentDtoCreated.getItemId() + "/comment")
                        .content(mapper.writeValueAsString(commentDtoToCreate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 11))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is(commentDtoCreated.getText()), String.class))
                .andExpect(jsonPath("$.authorName", is(commentDtoCreated.getAuthorName()), String.class))
                .andExpect(jsonPath("$.id", is(commentDtoCreated.getId()), Integer.class))
                .andExpect(jsonPath("$.itemId", is(commentDtoCreated.getItemId()), Integer.class));
    }

}