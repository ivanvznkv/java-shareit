package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserCreateRequest;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceImplIntegrationTest {

    @Autowired
    private UserService userService;

    @Test
    void createUser_shouldSaveAndReturnDto() {
        UserCreateRequest createRequest = new UserCreateRequest(null, "John Doe", "john@example.com");

        UserResponseDto response = userService.createUser(createRequest);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isNotNull();
        assertThat(response.getName()).isEqualTo("John Doe");
        assertThat(response.getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void createUser_duplicateEmail_shouldThrowException() {
        UserCreateRequest createRequest = new UserCreateRequest(null, "John", "john@example.com");
        userService.createUser(createRequest);

        UserCreateRequest duplicateRequest = new UserCreateRequest(null, "Jane", "john@example.com");

        assertThatThrownBy(() -> userService.createUser(duplicateRequest))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("Email уже используется");
    }

    @Test
    void updateUser_shouldUpdateFields() {
        UserCreateRequest createRequest = new UserCreateRequest(null, "John", "john@example.com");
        UserResponseDto created = userService.createUser(createRequest);

        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setName("John Updated");
        updateRequest.setEmail("john.updated@example.com");

        UserResponseDto updated = userService.updateUser(created.getId(), updateRequest);

        assertThat(updated.getName()).isEqualTo("John Updated");
        assertThat(updated.getEmail()).isEqualTo("john.updated@example.com");
    }

    @Test
    void updateUser_duplicateEmail_shouldThrow() {
        UserCreateRequest user1 = new UserCreateRequest(null, "User1", "user1@example.com");
        userService.createUser(user1);
        UserCreateRequest user2 = new UserCreateRequest(null, "User2", "user2@example.com");
        UserResponseDto created2 = userService.createUser(user2);

        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setEmail("user1@example.com");

        assertThatThrownBy(() -> userService.updateUser(created2.getId(), updateRequest))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("Email уже используется");
    }

    @Test
    void getUserById_shouldReturnUser() {
        UserCreateRequest createRequest = new UserCreateRequest(null, "John", "john@example.com");
        UserResponseDto created = userService.createUser(createRequest);

        UserResponseDto found = userService.getUserById(created.getId());

        assertThat(found.getId()).isEqualTo(created.getId());
    }

    @Test
    void getUserById_notFound_shouldThrow() {
        assertThatThrownBy(() -> userService.getUserById(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь не найден");
    }

    @Test
    void getAllUsers_shouldReturnList() {
        userService.createUser(new UserCreateRequest(null, "User1", "user1@example.com"));
        userService.createUser(new UserCreateRequest(null, "User2", "user2@example.com"));

        var users = userService.getAllUsers();

        assertThat(users).hasSize(2);
    }

    @Test
    void deleteUser_shouldRemoveUser() {
        UserCreateRequest createRequest = new UserCreateRequest(null, "John", "john@example.com");
        UserResponseDto created = userService.createUser(createRequest);

        userService.deleteUser(created.getId());

        assertThatThrownBy(() -> userService.getUserById(created.getId()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void deleteUser_notFound_shouldThrow() {
        assertThatThrownBy(() -> userService.deleteUser(999L))
                .isInstanceOf(NotFoundException.class);
    }
}
