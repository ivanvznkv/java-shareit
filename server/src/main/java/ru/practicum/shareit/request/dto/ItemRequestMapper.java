package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

    public static ItemRequestResponseDto toResponseDtoWithItems(ItemRequest itemRequest, List<Item> items) {
        ItemRequestResponseDto dto = toResponseDto(itemRequest);
        List<ItemForRequestDto> itemDtos = (items != null) ?
                items.stream().map(ItemRequestMapper::toItemForRequestDto).collect(Collectors.toList()) :
                Collections.emptyList();
        dto.setItems(itemDtos);
        return dto;
    }

    public static ItemForRequestDto toItemForRequestDto(Item item) {
        ItemForRequestDto dto = new ItemForRequestDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setOwnerId(item.getOwner().getId());
        return dto;
    }
}
