package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookItemRequestDtoTest {

    @Autowired
    private JacksonTester<BookItemRequestDto> json;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testSerialize() throws Exception {
        LocalDateTime start = LocalDateTime.of(2025, 1, 1, 10, 0);
        LocalDateTime end = LocalDateTime.of(2025, 1, 2, 10, 0);
        BookItemRequestDto dto = new BookItemRequestDto(1L, start, end);

        JsonContent<BookItemRequestDto> result = json.write(dto);

        assertThat(result).hasJsonPathNumberValue("$.itemId", 1);
        assertThat(result).hasJsonPathStringValue("$.start");
        assertThat(result).hasJsonPathStringValue("$.end");
    }

    @Test
    void testDeserialize() throws Exception {
        String content = "{\"itemId\":1,\"start\":\"2025-01-01T10:00:00\",\"end\":\"2025-01-02T10:00:00\"}";

        BookItemRequestDto dto = json.parseObject(content);

        assertThat(dto.getItemId()).isEqualTo(1L);
        assertThat(dto.getStart()).isEqualTo(LocalDateTime.parse("2025-01-01T10:00:00"));
        assertThat(dto.getEnd()).isEqualTo(LocalDateTime.parse("2025-01-02T10:00:00"));
    }
}
