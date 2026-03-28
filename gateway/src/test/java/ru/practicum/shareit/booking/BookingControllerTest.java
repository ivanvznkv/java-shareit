package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingClient bookingClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getBookings_shouldReturn200() throws Exception {
        when(bookingClient.getBookings(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(ResponseEntity.ok("[]"));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    void getBookings_withInvalidState_shouldReturn400() throws Exception {
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "UNKNOWN"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void bookItem_shouldReturn200() throws Exception {
        BookItemRequestDto requestDto = new BookItemRequestDto(
                1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );

        when(bookingClient.bookItem(anyLong(), any(BookItemRequestDto.class)))
                .thenReturn(ResponseEntity.ok("{\"id\":1}"));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());
    }

    @Test
    void bookItem_withInvalidDates_shouldReturn400() throws Exception {
        BookItemRequestDto invalidDto = new BookItemRequestDto(
                1L,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(2)
        );

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBooking_shouldReturn200() throws Exception {
        when(bookingClient.getBooking(1L, 1L))
                .thenReturn(ResponseEntity.ok("{\"id\":1}"));

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }
}
