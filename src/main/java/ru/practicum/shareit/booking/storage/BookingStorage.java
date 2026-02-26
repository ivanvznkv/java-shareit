package ru.practicum.shareit.booking.storage;

import ru.practicum.shareit.booking.model.Booking;

import java.util.Optional;

public interface BookingStorage {
    Booking create(Booking booking);

    Booking update(Booking booking);

    Optional<Booking> findById(Long id);
}
