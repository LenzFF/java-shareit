package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.booking.dto.BookingDto;

@RequiredArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class ItemWithBookingsDto extends ItemDto {

    private BookingDto lastBooking;

    private BookingDto nextBooking;
}
