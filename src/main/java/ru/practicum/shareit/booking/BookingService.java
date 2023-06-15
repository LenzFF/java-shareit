package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {
    Booking create(BookingDto bookingDto, Long userId);

    Booking changeStatus(Long userId, Long bookingId, boolean approved);

    Booking get(Long userId, Long bookingId);

    List<Booking> getUserBookingsByState(Long userId, String state);

    List<Booking> getOwnerBookingsByState(Long userId, String state);
}
