package ru.practicum.shareit.request.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateRequest;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestUpdateRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestResponseDto createItemRequest(@Valid @RequestBody ItemRequestCreateRequest createRequest,
                                                    @RequestHeader("X-Sharer-User-Id") Long requestorId) {
        return itemRequestService.createItemRequest(createRequest, requestorId);
    }

    @PatchMapping("/{requestId}")
    public ItemRequestResponseDto updateItemRequest(@PathVariable Long requestId,
                                                    @RequestBody ItemRequestUpdateRequest updateRequest) {
        return itemRequestService.updateItemRequest(requestId, updateRequest);
    }

    @GetMapping("/{requestId}")
    public ItemRequestResponseDto getItemRequestById(@PathVariable Long requestId) {
        return itemRequestService.getItemRequestById(requestId);
    }
}
