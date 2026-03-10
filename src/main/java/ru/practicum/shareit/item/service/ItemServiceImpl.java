package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;


    @Override
    public ItemResponseDto createItem(Long ownerId, ItemCreateRequest createRequest) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Item item = ItemMapper.fromCreateRequest(createRequest, owner, null);
        Item saved = itemRepository.save(item);
        return ItemMapper.toResponseDto(saved);
    }

    @Override
    public ItemResponseDto updateItem(Long itemId, Long ownerId, ItemUpdateRequest updateRequest) {
        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException("Пользователь не найден");
        }

        Item existing = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        if (!existing.getOwner().getId().equals(ownerId)) {
            throw new AccessDeniedException("Нет прав на изменение этой вещи");
        }

        ItemMapper.updateItemFromRequest(updateRequest, existing);
        Item updated = itemRepository.save(existing);
        return ItemMapper.toResponseDto(updated);
    }

    @Override
    public ItemDetailedDto getItemById(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        LocalDateTime now = LocalDateTime.now();
        boolean isOwner = item.getOwner().getId().equals(userId);

        BookingShortDto lastBooking = null;
        BookingShortDto nextBooking = null;

        if (isOwner) {
            List<Booking> lastBookings = bookingRepository.findLastBooking(itemId, now);
            if (!lastBookings.isEmpty()) {
                lastBooking = BookingMapper.toShortDto(lastBookings.get(0));
            }
            List<Booking> nextBookings = bookingRepository.findNextBooking(itemId, now);
            if (!nextBookings.isEmpty()) {
                nextBooking = BookingMapper.toShortDto(nextBookings.get(0));
            }
        }

        List<CommentResponseDto> comments = commentRepository.findByItemIdOrderByCreatedDesc(itemId).stream()
                .map(CommentMapper::toResponseDto)
                .collect(Collectors.toList());

        return ItemMapper.toDetailedDto(item, lastBooking, nextBooking, comments);
    }

    @Override
    public List<ItemWithBookingsDto> getItemsByOwner(Long ownerId) {
        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException("Пользователь не найден");
        }

        List<Item> items = itemRepository.findByOwnerId(ownerId);
        if (items.isEmpty()) {
            return Collections.emptyList();
        }

        LocalDateTime now = LocalDateTime.now();
        List<Long> itemIds = items.stream().map(Item::getId).collect(Collectors.toList());

        List<Booking> lastBookings = bookingRepository.findAllLastBookings(itemIds, now);
        Map<Long, BookingShortDto> lastBookingMap = lastBookings.stream()
                .collect(Collectors.groupingBy(
                        b -> b.getItem().getId(),
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> BookingMapper.toShortDto(list.get(0))
                        )
                ));

        List<Booking> nextBookings = bookingRepository.findAllNextBookings(itemIds, now);
        Map<Long, BookingShortDto> nextBookingMap = nextBookings.stream()
                .collect(Collectors.groupingBy(
                        b -> b.getItem().getId(),
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> BookingMapper.toShortDto(list.get(0))
                        )
                ));

        List<Comment> comments = commentRepository.findByItemIdIn(itemIds);
        Map<Long, List<CommentResponseDto>> commentsByItemId = comments.stream()
                .collect(Collectors.groupingBy(
                        c -> c.getItem().getId(),
                        Collectors.mapping(CommentMapper::toResponseDto, Collectors.toList())
                ));

        return items.stream()
                .map(item -> ItemMapper.toWithBookingsDto(
                        item,
                        lastBookingMap.get(item.getId()),
                        nextBookingMap.get(item.getId()),
                        commentsByItemId.getOrDefault(item.getId(), Collections.emptyList())
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemResponseDto> searchItems(String text) {
        if (text == null || text.trim().isEmpty()) {
            return List.of();
        }
        return itemRepository.search(text.trim()).stream()
                .map(ItemMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentResponseDto addComment(Long itemId, Long authorId, CommentCreateRequest request) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + itemId + " не найдена"));
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + authorId + " не найден"));

        LocalDateTime now = LocalDateTime.now();
        List<Booking> relevantBookings = bookingRepository.findByBookerIdAndItemIdAndStatusIn(
                authorId, itemId, List.of(BookingStatus.APPROVED, BookingStatus.WAITING));
        boolean hasCompletedBooking = relevantBookings.stream()
                .anyMatch(b -> !b.getEnd().isAfter(now));

        if (!hasCompletedBooking) {
            throw new ValidationException("Пользователь не может оставить отзыв, так как не брал вещь в аренду или аренда ещё не завершена");
        }

        Comment comment = CommentMapper.fromCreateRequest(request, item, author, now);
        Comment saved = commentRepository.save(comment);
        return CommentMapper.toResponseDto(saved);
    }
}
