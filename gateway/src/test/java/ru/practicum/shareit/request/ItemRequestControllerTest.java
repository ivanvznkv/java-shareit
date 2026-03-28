package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestCreateRequest;
import ru.practicum.shareit.request.dto.ItemRequestUpdateRequest;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestClient itemRequestClient;

    @Autowired
    private ObjectMapper objectMapper;

    private ItemRequestCreateRequest validCreateRequest;
    private ItemRequestUpdateRequest validUpdateRequest;

    @BeforeEach
    void setUp() {
        validCreateRequest = new ItemRequestCreateRequest("Нужен молоток");
        validUpdateRequest = new ItemRequestUpdateRequest();
        validUpdateRequest.setDescription("Нужна отвертка");
    }

    @Test
    void createRequest_shouldReturn200() throws Exception {
        when(itemRequestClient.createRequest(anyLong(), any(ItemRequestCreateRequest.class)))
                .thenReturn(ResponseEntity.ok("{\"id\":1}"));

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validCreateRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void createRequest_withEmptyDescription_shouldReturn400() throws Exception {
        ItemRequestCreateRequest invalid = new ItemRequestCreateRequest("");

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateRequest_shouldReturn200() throws Exception {
        when(itemRequestClient.updateRequest(anyLong(), any(ItemRequestUpdateRequest.class)))
                .thenReturn(ResponseEntity.ok("{\"id\":1}"));

        mockMvc.perform(patch("/requests/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUpdateRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void getOwnRequests_shouldReturn200() throws Exception {
        when(itemRequestClient.getOwnRequests(1L))
                .thenReturn(ResponseEntity.ok("[]"));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void getOtherRequests_shouldReturn200() throws Exception {
        when(itemRequestClient.getOtherRequests(1L))
                .thenReturn(ResponseEntity.ok("[]"));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void getRequestById_shouldReturn200() throws Exception {
        when(itemRequestClient.getRequestById(1L, 1L))
                .thenReturn(ResponseEntity.ok("{\"id\":1}"));

        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }
}
