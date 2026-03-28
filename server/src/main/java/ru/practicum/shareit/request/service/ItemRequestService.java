package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestCreateRequest;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestUpdateRequest;

import java.util.List;

public interface ItemRequestService {
    ItemRequestResponseDto createItemRequest(ItemRequestCreateRequest createRequest, Long requestorId);

    ItemRequestResponseDto updateItemRequest(Long requestId, ItemRequestUpdateRequest updateRequest);

    ItemRequestResponseDto getItemRequestById(Long requestId);

    List<ItemRequestResponseDto> getOwnRequests(Long userId);

    List<ItemRequestResponseDto> getOtherRequests(Long userId);
}
