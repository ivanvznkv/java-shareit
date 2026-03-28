package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateRequest;
import ru.practicum.shareit.request.dto.ItemRequestUpdateRequest;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @Valid @RequestBody ItemRequestCreateRequest request) {
        log.info("Creating request {}, userId={}", request, userId);
        return itemRequestClient.createRequest(userId, request);
    }

    @PatchMapping("/{requestId}")
    public ResponseEntity<Object> updateRequest(@PathVariable Long requestId,
                                                @RequestBody ItemRequestUpdateRequest request) {
        log.info("Updating request {} with {}", requestId, request);
        return itemRequestClient.updateRequest(requestId, request);
    }

    @GetMapping
    public ResponseEntity<Object> getOwnRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Get own requests for user {}", userId);
        return itemRequestClient.getOwnRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getOtherRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Get other requests for user {}", userId);
        return itemRequestClient.getOtherRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@PathVariable Long requestId,
                                                 @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Get request {} by userId={}", requestId, userId);
        return itemRequestClient.getRequestById(requestId, userId);
    }
}
