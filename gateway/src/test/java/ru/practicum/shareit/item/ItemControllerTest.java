package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentCreateRequest;
import ru.practicum.shareit.item.dto.ItemCreateRequest;
import ru.practicum.shareit.item.dto.ItemUpdateRequest;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemClient itemClient;

    @Autowired
    private ObjectMapper objectMapper;

    private ItemCreateRequest validCreateRequest;
    private ItemUpdateRequest validUpdateRequest;
    private CommentCreateRequest validCommentRequest;

    @BeforeEach
    void setUp() {
        validCreateRequest = new ItemCreateRequest("Молоток", "Тяжелый молоток", true, null);
        validUpdateRequest = new ItemUpdateRequest();
        validUpdateRequest.setName("Отвертка");
        validCommentRequest = new CommentCreateRequest();
        validCommentRequest.setText("Отличная вещь!");
    }

    @Test
    void createItem_shouldReturn200() throws Exception {
        when(itemClient.createItem(anyLong(), any(ItemCreateRequest.class)))
                .thenReturn(ResponseEntity.ok("{\"id\":1}"));

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validCreateRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void createItem_withInvalidData_shouldReturn400() throws Exception {
        ItemCreateRequest invalidRequest = new ItemCreateRequest("", "", null, null);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateItem_shouldReturn200() throws Exception {
        when(itemClient.updateItem(anyLong(), anyLong(), any(ItemUpdateRequest.class)))
                .thenReturn(ResponseEntity.ok("{\"id\":1}"));

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUpdateRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void getItemById_shouldReturn200() throws Exception {
        when(itemClient.getItemById(1L, 1L))
                .thenReturn(ResponseEntity.ok("{\"id\":1}"));

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void getItemsByOwner_shouldReturn200() throws Exception {
        when(itemClient.getItemsByOwner(1L))
                .thenReturn(ResponseEntity.ok("[{\"id\":1}]"));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void searchItems_shouldReturn200() throws Exception {
        when(itemClient.searchItems("молоток", 1L))
                .thenReturn(ResponseEntity.ok("[{\"id\":1}]"));

        mockMvc.perform(get("/items/search")
                        .param("text", "молоток")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void addComment_shouldReturn200() throws Exception {
        when(itemClient.addComment(anyLong(), anyLong(), any(CommentCreateRequest.class)))
                .thenReturn(ResponseEntity.ok("{\"id\":1}"));

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validCommentRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void addComment_withEmptyText_shouldReturn400() throws Exception {
        CommentCreateRequest invalid = new CommentCreateRequest();
        invalid.setText("");

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }
}
