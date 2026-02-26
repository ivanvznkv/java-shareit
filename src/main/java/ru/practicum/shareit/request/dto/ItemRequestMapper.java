package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

public class ItemRequestMapper {

    public static ItemRequestResponseDto toResponseDto(ItemRequest itemRequest) {
        ItemRequestResponseDto dto = new ItemRequestResponseDto();
        dto.setId(itemRequest.getId());
        dto.setDescription(itemRequest.getDescription());
        dto.setCreated(itemRequest.getCreated());
        return dto;
    }

    public static ItemRequest fromCreateRequest(ItemRequestCreateRequest createRequest, User requestor) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(createRequest.getDescription());
        itemRequest.setRequestor(requestor);
        itemRequest.setCreated(LocalDateTime.now());
        return itemRequest;
    }

    public static void updateRequestFromRequest(ItemRequestUpdateRequest updateRequest, ItemRequest itemRequest) {
        if (updateRequest.getDescription() != null) {
            itemRequest.setDescription(updateRequest.getDescription());
        }
    }
}
