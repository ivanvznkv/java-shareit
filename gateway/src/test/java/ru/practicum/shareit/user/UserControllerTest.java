package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserCreateRequest;
import ru.practicum.shareit.user.dto.UserUpdateRequest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserClient userClient;

    @Autowired
    private ObjectMapper objectMapper;

    private UserCreateRequest validCreateRequest;
    private UserUpdateRequest validUpdateRequest;

    @BeforeEach
    void setUp() {
        validCreateRequest = new UserCreateRequest("John Doe", "john@example.com");
        validUpdateRequest = new UserUpdateRequest();
        validUpdateRequest.setName("John Updated");
        validUpdateRequest.setEmail("john.updated@example.com");
    }

    @Test
    void createUser_shouldReturn201() throws Exception {
        when(userClient.createUser(any(UserCreateRequest.class)))
                .thenReturn(ResponseEntity.status(201).body("{\"id\":1}"));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validCreateRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    void createUser_withInvalidData_shouldReturn400() throws Exception {
        UserCreateRequest invalidRequest = new UserCreateRequest("", "invalid-email");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateUser_shouldReturn200() throws Exception {
        when(userClient.updateUser(any(Long.class), any(UserUpdateRequest.class)))
                .thenReturn(ResponseEntity.ok("{\"id\":1}"));

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUpdateRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void getUserById_shouldReturn200() throws Exception {
        when(userClient.getUserById(1L))
                .thenReturn(ResponseEntity.ok("{\"id\":1}"));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk());
    }

    @Test
    void getAllUsers_shouldReturn200() throws Exception {
        when(userClient.getAllUsers())
                .thenReturn(ResponseEntity.ok("[{\"id\":1}]"));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteUser_shouldReturn200() throws Exception {
        when(userClient.deleteUser(1L))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
    }
}
