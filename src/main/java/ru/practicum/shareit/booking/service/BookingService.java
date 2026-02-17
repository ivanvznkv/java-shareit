package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingCreateRequest;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.BookingUpdateRequest;

public interface BookingService {
    BookingResponseDto createBooking(BookingCreateRequest createRequest, Long bookerId);

    BookingResponseDto updateBooking(Long bookingId, BookingUpdateRequest updateRequest);

    BookingResponseDto getBookingById(Long bookingId);
}
