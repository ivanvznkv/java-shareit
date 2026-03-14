package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentCreateRequest;
import ru.practicum.shareit.item.dto.ItemCreateRequest;
import ru.practicum.shareit.item.dto.ItemUpdateRequest;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                             @Valid @RequestBody ItemCreateRequest request) {
        log.info("Creating item {}, ownerId={}", request, ownerId);
        return itemClient.createItem(ownerId, request);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@PathVariable Long itemId,
                                             @RequestHeader("X-Sharer-User-Id") long ownerId,
                                             @RequestBody ItemUpdateRequest request) {
        log.info("Updating item {} for owner {}, request={}", itemId, ownerId, request);
        return itemClient.updateItem(itemId, ownerId, request);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable Long itemId,
                                              @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Get item {} by userId={}", itemId, userId);
        return itemClient.getItemById(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsByOwner(@RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Get items for owner {}", ownerId);
        return itemClient.getItemsByOwner(ownerId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam String text,
                                              @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Search items with text {}, userId={}", text, userId);
        return itemClient.searchItems(text, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@PathVariable Long itemId,
                                             @RequestHeader("X-Sharer-User-Id") long authorId,
                                             @Valid @RequestBody CommentCreateRequest request) {
        log.info("Adding comment to item {} from user {}, comment={}", itemId, authorId, request);
        return itemClient.addComment(itemId, authorId, request);
    }
}
