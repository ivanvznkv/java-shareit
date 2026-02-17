package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingCreateRequest;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.BookingUpdateRequest;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.storage.InMemoryBookingStorage;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.InMemoryItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.InMemoryUserStorage;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final InMemoryBookingStorage bookingStorage;
    private final InMemoryUserStorage userStorage;
    private final InMemoryItemStorage itemStorage;

    @Override
    public BookingResponseDto createBooking(BookingCreateRequest createRequest, Long bookerId) {
        User booker = userStorage.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + bookerId + " не найден"));

        Item item = itemStorage.findById(createRequest.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь с id " + createRequest.getItemId() + " не найдена"));

        if (!Boolean.TRUE.equals(item.getAvailable())) {
            throw new ValidationException("Вещь с id " + createRequest.getItemId() + " недоступна для бронирования");
        }

        if (item.getOwner().getId().equals(bookerId)) {
            throw new AccessDeniedException("Пользователь не может бронировать свою собственную вещь");
        }

        Booking booking = BookingMapper.fromCreateRequest(createRequest, item, booker);
        booking.setStatus(BookingStatus.WAITING);
        Booking createdBooking = bookingStorage.create(booking);
        return BookingMapper.toResponseDto(createdBooking);
    }

    @Override
    public BookingResponseDto updateBooking(Long bookingId, BookingUpdateRequest updateRequest) {
        Booking existingBooking = bookingStorage.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id " + bookingId + " не найдено"));

        BookingMapper.updateBookingFromRequest(updateRequest, existingBooking);

        Booking updatedBooking = bookingStorage.update(existingBooking);
        return BookingMapper.toResponseDto(updatedBooking);
    }

    @Override
    public BookingResponseDto getBookingById(Long bookingId) {
        Booking booking = bookingStorage.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id " + bookingId + " не найдено"));
        return BookingMapper.toResponseDto(booking);
    }
}