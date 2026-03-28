package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {

    public static BookingResponseDto toResponseDto(Booking booking) {
        BookingResponseDto dto = new BookingResponseDto();
        dto.setId(booking.getId());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        dto.setItem(ItemMapper.toResponseDto(booking.getItem()));
        dto.setBooker(UserMapper.toResponseDto(booking.getBooker()));
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

    public static BookingShortDto toShortDto(Booking booking) {
        BookingShortDto dto = new BookingShortDto();
        dto.setId(booking.getId());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        dto.setBookerId(booking.getBooker().getId());
        return dto;
    }
}
