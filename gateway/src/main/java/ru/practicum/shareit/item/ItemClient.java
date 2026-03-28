package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentCreateRequest;
import ru.practicum.shareit.item.dto.ItemCreateRequest;
import ru.practicum.shareit.item.dto.ItemUpdateRequest;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> createItem(Long ownerId, ItemCreateRequest request) {
        return post("", ownerId, request);
    }

    public ResponseEntity<Object> updateItem(Long itemId, Long ownerId, ItemUpdateRequest request) {
        return patch("/" + itemId, ownerId, request);
    }

    public ResponseEntity<Object> getItemById(Long itemId, Long userId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getItemsByOwner(Long ownerId) {
        return get("", ownerId);
    }

    public ResponseEntity<Object> searchItems(String text, Long userId) {
        return get("/search?text=" + text, userId);
    }

    public ResponseEntity<Object> addComment(Long itemId, Long authorId, CommentCreateRequest request) {
        return post("/" + itemId + "/comment", authorId, request);
    }
}
