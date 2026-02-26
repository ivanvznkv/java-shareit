package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemCreateRequest;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateRequest;

import java.util.List;

public interface ItemService {
    ItemResponseDto createItem(Long ownerId, ItemCreateRequest createRequest);

    ItemResponseDto updateItem(Long itemId, Long ownerId, ItemUpdateRequest updateRequest);

    ItemResponseDto getItemById(Long itemId);

    List<ItemResponseDto> getItemsByOwner(Long ownerId);

    List<ItemResponseDto> searchItems(String text);
}
