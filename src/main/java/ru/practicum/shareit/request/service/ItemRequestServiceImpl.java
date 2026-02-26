package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestCreateRequest;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestUpdateRequest;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.storage.InMemoryItemRequestStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.InMemoryUserStorage;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final InMemoryItemRequestStorage requestStorage;
    private final InMemoryUserStorage userStorage;

    @Override
    public ItemRequestResponseDto createItemRequest(ItemRequestCreateRequest createRequest, Long requestorId) {
        User requestor = userStorage.findById(requestorId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + requestorId + " не найден"));

        ItemRequest itemRequest = ItemRequestMapper.fromCreateRequest(createRequest, requestor);
        ItemRequest createdRequest = requestStorage.create(itemRequest);
        return ItemRequestMapper.toResponseDto(createdRequest);
    }

    @Override
    public ItemRequestResponseDto updateItemRequest(Long requestId, ItemRequestUpdateRequest updateRequest) {
        ItemRequest existingRequest = requestStorage.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос вещи с id " + requestId + " не найден"));

        ItemRequestMapper.updateRequestFromRequest(updateRequest, existingRequest);

        ItemRequest updatedRequest = requestStorage.update(existingRequest);
        return ItemRequestMapper.toResponseDto(updatedRequest);
    }

    @Override
    public ItemRequestResponseDto getItemRequestById(Long requestId) {
        ItemRequest itemRequest = requestStorage.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос вещи с id " + requestId + " не найден"));
        return ItemRequestMapper.toResponseDto(itemRequest);
    }
}
