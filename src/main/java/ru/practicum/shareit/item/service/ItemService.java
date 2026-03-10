package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {
    ItemResponseDto createItem(Long ownerId, ItemCreateRequest createRequest);

    ItemResponseDto updateItem(Long itemId, Long ownerId, ItemUpdateRequest updateRequest);

    ItemDetailedDto getItemById(Long itemId, Long userId);

    List<ItemWithBookingsDto> getItemsByOwner(Long ownerId);

    List<ItemResponseDto> searchItems(String text);

    CommentResponseDto addComment(Long itemId, Long authorId, CommentCreateRequest request);
}
