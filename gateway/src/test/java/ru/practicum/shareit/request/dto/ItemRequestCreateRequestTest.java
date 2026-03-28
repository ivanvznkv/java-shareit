package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestCreateRequestTest {

    @Autowired
    private JacksonTester<ItemRequestCreateRequest> json;

    @Test
    void testSerialize() throws Exception {
        ItemRequestCreateRequest dto = new ItemRequestCreateRequest("Нужен молоток");

        JsonContent<ItemRequestCreateRequest> result = json.write(dto);

        assertThat(result).hasJsonPathStringValue("$.description", "Нужен молоток");
    }

    @Test
    void testDeserialize() throws Exception {
        String content = "{\"description\":\"Нужен молоток\"}";

        ItemRequestCreateRequest dto = json.parseObject(content);

        assertThat(dto.getDescription()).isEqualTo("Нужен молоток");
    }
}
