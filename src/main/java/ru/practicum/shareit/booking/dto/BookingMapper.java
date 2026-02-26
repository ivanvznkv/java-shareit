package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {

    public static BookingResponseDto toResponseDto(Booking booking) {
        BookingResponseDto dto = new BookingResponseDto();
        dto.setId(booking.getId());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        dto.setItemId(booking.getItem() != null ? booking.getItem().getId() : null);
        dto.setBookerId(booking.getBooker() != null ? booking.getBooker().getId() : null);
        dto.setStatus(booking.getStatus());
        return dto;
    }

    public static Booking fromCreateRequest(BookingCreateRequest bookingCreateRequest, Item item, User booker) {
        Booking booking = new Booking();
        booking.setStart(bookingCreateRequest.getStart());
        booking.setEnd(bookingCreateRequest.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);
        return booking;
    }

    public static void updateBookingFromRequest(BookingUpdateRequest request, Booking booking) {
        if (request.getStart() != null) {
            booking.setStart(request.getStart());
        }
        if (request.getEnd() != null) {
            booking.setEnd(request.getEnd());
        }
        if (request.getStatus() != null) {
            booking.setStatus(request.getStatus());
        }
    }
}
