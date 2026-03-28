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
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ItemServiceImplIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private Long ownerId;
    private Long requestId;

    @BeforeEach
    void setUp() {
        User owner = new User();
        owner.setName("Владелец");
        owner.setEmail("owner@example.com");
        owner = userRepository.save(owner);
        ownerId = owner.getId();

        User requester = new User();
        requester.setName("Заявитель");
        requester.setEmail("requester@example.com");
        requester = userRepository.save(requester);

        ItemRequest request = new ItemRequest();
        request.setDescription("Нужен инструмент");
        request.setRequestor(requester);
        request.setCreated(LocalDateTime.now());
        request = itemRequestRepository.save(request);
        requestId = request.getId();
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
