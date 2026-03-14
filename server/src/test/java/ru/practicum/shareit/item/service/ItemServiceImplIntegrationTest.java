package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemCreateRequest;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestCreateRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserCreateRequest;
import ru.practicum.shareit.user.service.UserService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ItemServiceImplIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemRequestService itemRequestService;

    private Long ownerId;
    private Long requestId;

    @BeforeEach
    void setUp() {
        UserCreateRequest ownerRequest = new UserCreateRequest(null, "Владелец", "owner@example.com");
        ownerId = userService.createUser(ownerRequest).getId();

        UserCreateRequest requesterRequest = new UserCreateRequest(null, "Заявитель", "requester@example.com");
        Long requesterId = userService.createUser(requesterRequest).getId();
        ItemRequestCreateRequest reqCreate = new ItemRequestCreateRequest("Нужен инструмент");
        requestId = itemRequestService.createItemRequest(reqCreate, requesterId).getId();
    }

    @Test
    void createItem_withRequestId_shouldLinkToRequest() {
        ItemCreateRequest createRequest = new ItemCreateRequest("Молоток", "Тяжелый молоток", true, requestId);

        ItemResponseDto response = itemService.createItem(ownerId, createRequest);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isNotNull();
        assertThat(response.getRequestId()).isEqualTo(requestId);
    }

    @Test
    void createItem_withoutRequestId_shouldNotLink() {
        ItemCreateRequest createRequest = new ItemCreateRequest("Молоток", "Тяжелый молоток", true, null);

        ItemResponseDto response = itemService.createItem(ownerId, createRequest);

        assertThat(response.getRequestId()).isNull();
    }

    @Test
    void createItem_withInvalidRequestId_shouldThrowNotFound() {
        ItemCreateRequest createRequest = new ItemCreateRequest("Молоток", "Тяжелый молоток", true, 999L);

        assertThatThrownBy(() -> itemService.createItem(ownerId, createRequest))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Запрос с id 999 не найден");
    }
}
