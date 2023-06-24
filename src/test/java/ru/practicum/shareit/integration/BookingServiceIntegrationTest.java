package ru.practicum.shareit.integration;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest()
public class BookingServiceIntegrationTest {

    private final UserService userService;
    private final ItemService itemService;
    private final BookingService bookingService;

    public static UserDto userDto1, userDto2;
    public ItemDto itemDto1;
    private BookingDto bookingDto1, bookingDto2, bookingDto3;
    private CommentDto commentDto;
    private ItemDto itemDto2, itemDto3;

    @BeforeEach
    void setUp() {
        userDto1 = new UserDto();
        userDto1.setEmail("user1@yandex.ru");
        userDto1.setName("User1");

        userDto2 = new UserDto();
        userDto2.setEmail("user2@yandex.ru");
        userDto2.setName("User2");

        itemDto1 = new ItemDto();
        itemDto1.setDescription("описание вещи поиск ");
        itemDto1.setName("имя вещи");
        itemDto1.setAvailable(true);

        itemDto2 = new ItemDto();
        itemDto2.setDescription("1описание вещи");
        itemDto2.setName("1имя вещи");
        itemDto2.setAvailable(true);

        itemDto3 = new ItemDto();
        itemDto3.setDescription("2описание вещи поисковик");
        itemDto3.setName("2имя вещи");
        itemDto3.setAvailable(true);

        bookingDto1 = new BookingDto();
        bookingDto1.setStart(LocalDateTime.now().plusSeconds(1));
        bookingDto1.setEnd(LocalDateTime.now().plusSeconds(2));

        bookingDto2 = new BookingDto();
        bookingDto2.setStart(LocalDateTime.now().plusSeconds(3));
        bookingDto2.setEnd(LocalDateTime.now().plusSeconds(4));

        bookingDto3 = new BookingDto();
        bookingDto3.setStart(LocalDateTime.now().plusSeconds(5));
        bookingDto3.setEnd(LocalDateTime.now().plusSeconds(6));

        commentDto = new CommentDto();
        commentDto.setText("text text");
    }

    @Test
    void bigBookingTest() {
        UserDto booker = userService.create(userDto1);
        UserDto owner = userService.create(userDto2);

        ItemDto newItemDto = itemService.create(owner.getId(), itemDto1);

        bookingDto1.setItemId(newItemDto.getId());

        //тест на создание
        Booking booking = bookingService.create(bookingDto1, booker.getId());
        assertThat(booking.getItem().getName(), equalTo(newItemDto.getName()));

        //проверка на ошибку создания бронирования, если available = false
        newItemDto.setAvailable(false);
        itemService.update(owner.getId(), newItemDto);

        assertThrows(ValidationException.class,
                () -> bookingService.create(bookingDto1, booker.getId()));

        //делаем запрос бронирования
        booking = bookingService.get(booker.getId(), booking.getId());
        assertThat(booking.getItem().getName(), equalTo(newItemDto.getName()));
        assertThat(booking.getStatus(), equalTo(BookingStatus.WAITING));

        //проверка на несуществующие id при запросе
        assertThrows(DataNotFoundException.class,
                () -> bookingService.get(99L, booker.getId()));
        assertThrows(DataNotFoundException.class,
                () -> bookingService.get(booker.getId(), 99L));

        //обновляем статус бронирования
        Booking updatedBooking = bookingService.changeStatus(owner.getId(), booking.getId(), true);

        assertThat(updatedBooking.getId(), equalTo(booking.getId()));
        assertThat(updatedBooking.getStatus(), equalTo(BookingStatus.APPROVED));

        //ошибка при повторном обновлении
        assertThrows(ValidationException.class,
                () -> bookingService.changeStatus(owner.getId(), updatedBooking.getId(), false));

        //добавляем бронирования и делаем запросы от автора бронирований с разными статусами
        newItemDto.setAvailable(true);
        itemService.update(owner.getId(), newItemDto);

        bookingDto1.setItemId(newItemDto.getId());
        bookingDto2.setItemId(newItemDto.getId());
        bookingDto3.setItemId(newItemDto.getId());
        Booking booking1 = bookingService.create(bookingDto1, booker.getId());
        Booking booking2 = bookingService.create(bookingDto2, booker.getId());
        Booking booking3 = bookingService.create(bookingDto3, booker.getId());

        bookingService.changeStatus(owner.getId(), booking1.getId(), false);

        List<Booking> rejectedBookings = bookingService.getUserBookingsByState(booker.getId(), "REJECTED", 0, 5);
        List<Booking> waitingBookings = bookingService.getUserBookingsByState(booker.getId(), "WAITING", 0, 2);
        List<Booking> allBookings = bookingService.getUserBookingsByState(booker.getId(), "ALL", 0, 5);
        List<Booking> allbookingsSize2 = bookingService.getUserBookingsByState(booker.getId(), "ALL", 0, 2);

        assertThat(rejectedBookings.size(), equalTo(1));
        assertThat(waitingBookings.size(), equalTo(2));
        assertThat(allBookings.size(), equalTo(4));
        assertThat(allbookingsSize2.size(), equalTo(2));

        //ловим ошибки пагинации, неверного статуса и неверного id пользователя
        assertThrows(RuntimeException.class,
                () -> bookingService.getUserBookingsByState(booker.getId(), "REJECTED", -1, 5));
        assertThrows(RuntimeException.class,
                () -> bookingService.getUserBookingsByState(booker.getId(), "REJECTED", 0, -1));
        assertThrows(DataNotFoundException.class,
                () -> bookingService.getUserBookingsByState(99L, "REJECTED", 0, 5));
        assertThrows(ValidationException.class,
                () -> bookingService.getUserBookingsByState(booker.getId(), "wrong", 0, 5));


        //бронирования от владельца вещи с разными статусами
        ItemDto newItemDto2 = itemService.create(booker.getId(), itemDto3);
        bookingDto1.setItemId(newItemDto.getId());
        bookingDto2.setItemId(newItemDto2.getId());
        bookingDto3.setItemId(newItemDto2.getId());
        booking1 = bookingService.create(bookingDto1, booker.getId());
        booking2 = bookingService.create(bookingDto2, owner.getId());
        booking3 = bookingService.create(bookingDto3, owner.getId());

        bookingService.changeStatus(booker.getId(), booking2.getId(), false);

        rejectedBookings = bookingService.getOwnerBookingsByState(booker.getId(), "REJECTED", 0, 5);
        waitingBookings = bookingService.getOwnerBookingsByState(booker.getId(), "WAITING", 0, 2);
        allBookings = bookingService.getOwnerBookingsByState(booker.getId(), "ALL", 0, 5);
        allbookingsSize2 = bookingService.getOwnerBookingsByState(booker.getId(), "ALL", 0, 1);

        assertThat(rejectedBookings.size(), equalTo(1));
        assertThat(waitingBookings.size(), equalTo(1));
        assertThat(allBookings.size(), equalTo(2));
        assertThat(allbookingsSize2.size(), equalTo(1));

        //ловим ошибки пагинации, неверного статуса и неверного id пользователя
        assertThrows(RuntimeException.class,
                () -> bookingService.getOwnerBookingsByState(booker.getId(), "REJECTED", -1, 5));
        assertThrows(RuntimeException.class,
                () -> bookingService.getOwnerBookingsByState(booker.getId(), "REJECTED", 0, -1));
        assertThrows(DataNotFoundException.class,
                () -> bookingService.getOwnerBookingsByState(99L, "REJECTED", 0, 5));
        assertThrows(ValidationException.class,
                () -> bookingService.getOwnerBookingsByState(booker.getId(), "wrong", 0, 5));
    }
}
