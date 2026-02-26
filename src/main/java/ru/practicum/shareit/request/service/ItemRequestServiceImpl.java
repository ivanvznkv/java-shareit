package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.*;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;

    @Override
    public ItemRequestResponseDto createItemRequest(ItemRequestCreateRequest createRequest, Long requestorId) {
        User requestor = userRepository.findById(requestorId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + requestorId + " не найден"));

        ItemRequest itemRequest = ItemRequestMapper.fromCreateRequest(createRequest, requestor);
        ItemRequest saved = requestRepository.save(itemRequest);
        return ItemRequestMapper.toResponseDto(saved);
    }

    @Override
    public ItemRequestResponseDto updateItemRequest(Long requestId, ItemRequestUpdateRequest updateRequest) {
        ItemRequest existing = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос вещи с id " + requestId + " не найден"));

        ItemRequestMapper.updateRequestFromRequest(updateRequest, existing);
        ItemRequest updated = requestRepository.save(existing);
        return ItemRequestMapper.toResponseDto(updated);
    }

    @Override
    public ItemRequestResponseDto getItemRequestById(Long requestId) {
        ItemRequest itemRequest = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос вещи с id " + requestId + " не найден"));
        return ItemRequestMapper.toResponseDto(itemRequest);
    }
}
