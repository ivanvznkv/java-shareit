package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.item.dto.ItemCreateRequest;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateRequest;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.InMemoryItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.InMemoryUserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final InMemoryItemStorage itemStorage;
    private final InMemoryUserStorage userStorage;

    @Override
    public ItemResponseDto createItem(Long ownerId, ItemCreateRequest createRequest) {
        User owner = userStorage.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + ownerId + " не найден"));

        Item item = ItemMapper.fromCreateRequest(createRequest, owner, null);
        Item createdItem = itemStorage.create(item);
        return ItemMapper.toResponseDto(createdItem);
    }

    @Override
    public ItemResponseDto updateItem(Long itemId, Long ownerId, ItemUpdateRequest updateRequest) {
        User owner = userStorage.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + ownerId + " не найден"));

        Item existingItem = itemStorage.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + itemId + " не найдена"));

        if (!existingItem.getOwner().getId().equals(ownerId)) {
            throw new AccessDeniedException("Пользователь с id " + ownerId + " не является владельцем вещи с id " + itemId);
        }

        ItemMapper.updateItemFromRequest(updateRequest, existingItem);

        Item updatedItem = itemStorage.update(existingItem);
        return ItemMapper.toResponseDto(updatedItem);
    }

    @Override
    public ItemResponseDto getItemById(Long itemId) {
        Item item = itemStorage.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + itemId + " не найдена"));
        return ItemMapper.toResponseDto(item);
    }

    @Override
    public List<ItemResponseDto> getItemsByOwner(Long ownerId) {
        userStorage.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + ownerId + " не найден"));

        return itemStorage.findByOwner(ownerId).stream()
                .map(ItemMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemResponseDto> searchItems(String text) {
        if (text == null || text.trim().isEmpty()) {
            return List.of();
        }
        return itemStorage.search(text.trim()).stream()
                .map(ItemMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}
