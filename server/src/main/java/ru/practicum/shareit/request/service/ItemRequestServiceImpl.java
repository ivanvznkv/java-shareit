package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.*;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestResponseDto createItemRequest(ItemRequestCreateRequest createRequest, Long requestorId) {
        User requestor = userRepository.findById(requestorId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + requestorId + " не найден"));

        ItemRequest itemRequest = ItemRequestMapper.fromCreateRequest(createRequest, requestor);
        ItemRequest saved = requestRepository.save(itemRequest);
        return ItemRequestMapper.toResponseDto(saved);
    }

    @Override
    public ItemRequestResponseDto updateItemRequest(Long requestId, ItemRequestUpdateRequest updateRequest) {
        ItemRequest existing = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос вещи с id " + requestId + " не найден"));

        ItemRequestMapper.updateRequestFromRequest(updateRequest, existing);
        ItemRequest updated = requestRepository.save(existing);
        return ItemRequestMapper.toResponseDto(updated);
    }

    @Override
    public ItemRequestResponseDto getItemRequestById(Long requestId) {
        ItemRequest itemRequest = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос вещи с id " + requestId + " не найден"));
        List<Item> items = itemRepository.findByRequestId(requestId);
        return ItemRequestMapper.toResponseDtoWithItems(itemRequest, items);
    }

    @Override
    public List<ItemRequestResponseDto> getOwnRequests(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
        List<ItemRequest> requests = requestRepository.findByRequestorIdOrderByCreatedDesc(userId);
        return requests.stream()
                .map(request -> {
                    List<Item> items = itemRepository.findByRequestId(request.getId());
                    return ItemRequestMapper.toResponseDtoWithItems(request, items);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestResponseDto> getOtherRequests(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
        List<ItemRequest> requests = requestRepository.findByRequestorIdNotOrderByCreatedDesc(userId);
        return requests.stream()
                .map(request -> {
                    List<Item> items = itemRepository.findByRequestId(request.getId());
                    return ItemRequestMapper.toResponseDtoWithItems(request, items);
                })
                .collect(Collectors.toList());
    }
}
