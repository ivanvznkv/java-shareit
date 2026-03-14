package ru.practicum.shareit.request.storage;

import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Optional;

public interface ItemRequestStorage {
    ItemRequest create(ItemRequest request);

    ItemRequest update(ItemRequest request);

    Optional<ItemRequest> findById(Long id);
}
