package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingStorage;
    private final UserRepository userStorage;
    private final ItemRepository itemStorage;


    @Transactional
    @Override
    public Booking create(BookingDto bookingDto, Long userId) {
        Booking booking = new Booking();

        booking.setItem(itemStorage.findById(bookingDto.getItemId())
                .orElseThrow(() -> new DataNotFoundException("Вещь не найдена, id - " + bookingDto.getItemId())));

        if (!booking.getItem().getAvailable()) {
            throw new ValidationException("Вещь не доступна для бронирования, id - " + bookingDto.getItemId());
        }

        if (booking.getItem().getId() == userId) {
            throw new DataNotFoundException("Пользователь не может забронировать свою вещь, id - " + userId);
        }

        booking.setBooker(userStorage.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Пользователь не найден, id - " + userId)));

        if (bookingDto.getStart().isAfter(bookingDto.getEnd())
                || bookingDto.getStart() == null
                || bookingDto.getEnd() == null
                || bookingDto.getStart().isEqual(bookingDto.getEnd())) {
            throw new ValidationException("Ошибка ввода времени");
        }

        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());

        return bookingStorage.save(booking);
    }

    @Transactional
    @Override
    public Booking changeStatus(Long userId, Long bookingId, boolean approved) {
        userStorage.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Пользователь не найден, id - " + userId));


        Booking booking = bookingStorage.findById(bookingId)
                .orElseThrow(() -> new DataNotFoundException("Бронирование не найдено>, id - " + bookingId));

        if (booking.getItem().getOwner().getId() != userId) {
            throw new DataNotFoundException("Пользователю не принадлежит данная вещь");
        }

        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new ValidationException("Статус нельзя изменить");
        }

        booking.setStatus(BookingStatus.APPROVED);
        if (!approved) booking.setStatus(BookingStatus.REJECTED);

        bookingStorage.save(booking);

        return booking;
    }

    @Override
    public Booking get(Long userId, Long bookingId) {
        userStorage.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Пользователь не найден, id - " + userId));

        Booking booking = bookingStorage.findById(bookingId)
                .orElseThrow(() -> new DataNotFoundException("Вещь не найдена, id - " + bookingId));

        if (booking.getItem().getOwner().getId() != userId && booking.getBooker().getId() != userId) {
            throw new DataNotFoundException("Пользователь не может запрашивать данные об этой вещи");
        }

        return booking;
    }

    @Override
    public List<Booking> getUserBookingsByState(Long userId, String state) {

        userStorage.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Пользователь не найден, id - " + userId));

        switch (state) {
            case "ALL":
                return bookingStorage.findAllUserBookings(userId);

            case "CURRENT":
                return bookingStorage.findBookerCurrentBookings(userId);

            case "PAST":
                return bookingStorage.findBookerPastBookings(userId);

            case "FUTURE":
                return bookingStorage.findBookerFutureBookings(userId);

            case "REJECTED":
                return bookingStorage.findByBooker_IdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);

            case "WAITING":
                return bookingStorage.findByBooker_IdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);

            default:
                throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");

        }
    }

    @Override
    public List<Booking> getOwnerBookingsByState(Long userId, String state) {

        userStorage.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Пользователь не найден, id - " + userId));

        switch (state) {
            case "ALL":
                return bookingStorage.findByItem_Owner_IdOrderByStartDesc(userId);

            case "CURRENT":
                return bookingStorage.findOwnerCurrentBookings(userId);

            case "PAST":
                return bookingStorage.findOwnerPastBookings(userId);

            case "FUTURE":
                return bookingStorage.findOwnerFutureBookings(userId);

            case "REJECTED":
                return bookingStorage.findByItem_Owner_IdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);

            case "WAITING":
                return bookingStorage.findByItem_Owner_IdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);

            default:
                throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");

        }
    }
}
