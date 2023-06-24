package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {
    Booking create(BookingDto bookingDto, long userId);

    Booking changeStatus(long userId, long bookingId, boolean approved);

    Booking get(long userId, long bookingId);

    List<Booking> getUserBookingsByState(long userId, String state, int from, int size);

    List<Booking> getOwnerBookingsByState(long userId, String state, int from, int size);
}
