package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestCreateRequest;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.user.dto.UserCreateRequest;
import ru.practicum.shareit.user.service.UserService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ItemRequestServiceImplIntegrationTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private UserService userService;

    private Long userId;

    @BeforeEach
    void setUp() {
        UserCreateRequest userRequest = new UserCreateRequest(null, "Тестовый пользователь", "test@example.com");
        userId = userService.createUser(userRequest).getId();
    }

    @Test
    void createItemRequest_shouldSaveAndReturnDto() {
        ItemRequestCreateRequest createRequest = new ItemRequestCreateRequest("Нужен молоток");

        ItemRequestResponseDto response = itemRequestService.createItemRequest(createRequest, userId);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isNotNull();
        assertThat(response.getDescription()).isEqualTo("Нужен молоток");
        assertThat(response.getCreated()).isNotNull();
        assertThat(response.getItems()).isNull();
    }

    @Test
    void getOwnRequests_shouldReturnListWithItems() {
        ItemRequestCreateRequest createRequest = new ItemRequestCreateRequest("Нужна дрель");
        itemRequestService.createItemRequest(createRequest, userId);

        var ownRequests = itemRequestService.getOwnRequests(userId);

        assertThat(ownRequests).hasSize(1);
        ItemRequestResponseDto dto = ownRequests.get(0);
        assertThat(dto.getDescription()).isEqualTo("Нужна дрель");
        assertThat(dto.getItems()).isNotNull().isEmpty();
    }

    @Test
    void getOtherRequests_shouldReturnRequestsOfOtherUsers() {
        UserCreateRequest otherUserRequest = new UserCreateRequest(null, "Другой пользователь", "other@example.com");
        Long otherUserId = userService.createUser(otherUserRequest).getId();

        ItemRequestCreateRequest createRequest = new ItemRequestCreateRequest("Нужна пила");
        itemRequestService.createItemRequest(createRequest, otherUserId);

        var otherRequests = itemRequestService.getOtherRequests(userId);

        assertThat(otherRequests).hasSize(1);
        assertThat(otherRequests.get(0).getDescription()).isEqualTo("Нужна пила");
    }

    @Test
    void getRequestById_shouldReturnRequestWithItems() {
        ItemRequestCreateRequest createRequest = new ItemRequestCreateRequest("Нужна лестница");
        var created = itemRequestService.createItemRequest(createRequest, userId);

        var found = itemRequestService.getItemRequestById(created.getId());

        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(created.getId());
        assertThat(found.getDescription()).isEqualTo("Нужна лестница");
    }

    @Test
    void getRequestById_shouldThrowNotFoundWhenIdInvalid() {
        assertThatThrownBy(() -> itemRequestService.getItemRequestById(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Запрос вещи с id 999 не найден");
    }
}
