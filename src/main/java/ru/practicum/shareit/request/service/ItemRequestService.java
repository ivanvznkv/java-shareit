package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestCreateRequest;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestUpdateRequest;

public interface ItemRequestService {
    ItemRequestResponseDto createItemRequest(ItemRequestCreateRequest createRequest, Long requestorId);

    ItemRequestResponseDto updateItemRequest(Long requestId, ItemRequestUpdateRequest updateRequest);

    ItemRequestResponseDto getItemRequestById(Long requestId);
}
