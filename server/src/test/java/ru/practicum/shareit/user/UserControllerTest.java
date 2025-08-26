package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.UserEmailConfilct;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserService userService;

    @Test
    public void testCreateUser() throws Exception {
        UserDto userToCreate = UserDto.builder()
                .name("name")
                .email("email@email.com")
                .build();
        UserDto userCreated = UserDto.builder()
                .name("name")
                .email("email@email.com")
                .id(1)
                .build();
        when(userService.create(any()))
                .thenReturn(userCreated);

        mvc.perform(post("/users")
                    .content(mapper.writeValueAsString(userToCreate))
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userCreated.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(userCreated.getName()), String.class))
                .andExpect(jsonPath("$.email", is(userCreated.getEmail()), String.class));
    }

    @Test
    public void testUpdateUser() throws Exception {
        UserDto userToUpdate = UserDto.builder()
                .name("new name")
                .build();
        UserDto userUpdated = UserDto.builder()
                .name("new name")
                .email("email@email.com")
                .id(1)
                .build();
        when(userService.update(anyInt(), any()))
                .thenReturn(userUpdated);

        mvc.perform(patch("/users/" + userUpdated.getId())
                        .content(mapper.writeValueAsString(userToUpdate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userUpdated.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(userToUpdate.getName()), String.class))
                .andExpect(jsonPath("$.email", is(userUpdated.getEmail()), String.class));
    }

    @Test
    public void testDeleteUser() throws Exception {
        mvc.perform(delete("/users/" + 1))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetUserById() throws Exception {
        UserDto user = UserDto.builder()
                .name("new name")
                .email("email@email.com")
                .id(1)
                .build();
        when(userService.getUserById(anyInt()))
                .thenReturn(user);

        mvc.perform(get("/users/" + user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(user.getName()), String.class))
                .andExpect(jsonPath("$.email", is(user.getEmail()), String.class));
    }

    @Test
    public void testGetUsers() throws Exception {
        UserDto user1 = UserDto.builder()
                .name("new name")
                .email("email@email.com")
                .id(1)
                .build();
        UserDto user2 = UserDto.builder()
                .name("new name 2")
                .email("email2@email.com")
                .id(2)
                .build();
        when(userService.getAllUsers())
                .thenReturn(List.of(user1, user2));

        mvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(1), Integer.class))
                .andExpect(jsonPath("$.[1].id", is(2), Integer.class));
    }

    @Test
    public void testEmailDuplication() throws Exception {
        when(userService.create(any()))
                .thenThrow(new UserEmailConfilct("email conflict"));

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(UserDto.builder()
                                .email("email@email.com")
                                .build()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(409));
    }

    @Test
    public void testBadValidation() throws Exception {
        when(userService.create(any()))
                .thenThrow(new ValidationException("bad validation"));

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(UserDto.builder()
                                .email("email@email.com")
                                .build()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    public void testSomeErrorOnCreation() throws Exception {
        when(userService.create(any()))
                .thenThrow(new RuntimeException("some error"));

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(UserDto.builder()
                                .email("email@email.com")
                                .build()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(500));
    }
}