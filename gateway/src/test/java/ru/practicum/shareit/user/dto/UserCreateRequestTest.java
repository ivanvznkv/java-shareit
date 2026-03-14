package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserCreateRequestTest {

    @Autowired
    private JacksonTester<UserCreateRequest> json;

    @Test
    void testSerialize() throws Exception {
        UserCreateRequest dto = new UserCreateRequest("John", "john@example.com");

        JsonContent<UserCreateRequest> result = json.write(dto);

        assertThat(result).hasJsonPathStringValue("$.name", "John");
        assertThat(result).hasJsonPathStringValue("$.email", "john@example.com");
    }

    @Test
    void testDeserialize() throws Exception {
        String content = "{\"name\":\"John\",\"email\":\"john@example.com\"}";

        UserCreateRequest dto = json.parseObject(content);

        assertThat(dto.getName()).isEqualTo("John");
        assertThat(dto.getEmail()).isEqualTo("john@example.com");
    }
}
