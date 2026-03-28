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
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ItemRequestServiceImplIntegrationTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private UserRepository userRepository;

    private Long userId;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setName("Тестовый пользователь");
        user.setEmail("test@example.com");
        user = userRepository.save(user);
        userId = user.getId();
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
        User otherUser = new User();
        otherUser.setName("Другой пользователь");
        otherUser.setEmail("other@example.com");
        otherUser = userRepository.save(otherUser);
        Long otherUserId = otherUser.getId();

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
