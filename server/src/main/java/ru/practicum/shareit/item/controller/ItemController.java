package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemResponseDto createItem(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                      @RequestBody ItemCreateRequest createRequest) {
        return itemService.createItem(ownerId, createRequest);
    }

    @PatchMapping("/{itemId}")
    public ItemResponseDto updateItem(@PathVariable Long itemId,
                                      @RequestHeader("X-Sharer-User-Id") Long ownerId,
                                      @RequestBody ItemUpdateRequest updateRequest) {
        return itemService.updateItem(itemId, ownerId, updateRequest);
    }

    @GetMapping("/{itemId}")
    public ItemDetailedDto getItemById(@PathVariable Long itemId,
                                       @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping
    public List<ItemWithBookingsDto> getItemsByOwner(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return itemService.getItemsByOwner(ownerId);
    }

    @GetMapping("/search")
    public List<ItemResponseDto> searchItems(@RequestParam String text,
                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.searchItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponseDto addComment(@PathVariable Long itemId,
                                         @RequestBody CommentCreateRequest commentDto,
                                         @RequestHeader("X-Sharer-User-Id") Long authorId) {
        return itemService.addComment(itemId, authorId, commentDto);
    }
}
