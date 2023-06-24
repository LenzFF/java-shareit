package ru.practicum.shareit.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingServiceImpl;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    public UserDto ownerDto, bookerDto;
    public ItemDto itemDto;
    public Item item1, item2, item3;
    public BookingDto bookingDto1, bookingDto2, bookingDto3;
    public Booking booking1, booking2, booking3;
    public List<Booking> bookingList;
    @Mock
    static UserRepository userRepository;
    @Mock
    static ItemRepository itemRepository;
    @Mock
    static BookingRepository mockBookingRepository;

    @InjectMocks
    public BookingServiceImpl bookingService;

    @BeforeEach
    void setUp() {
        ownerDto = new UserDto();
        ownerDto.setId(11L);
        ownerDto.setEmail("mail@mail.mail");
        ownerDto.setName("myName");

        bookerDto = new UserDto();
        bookerDto.setId(12L);
        bookerDto.setEmail("12mail@mail.mail");
        bookerDto.setName("12myName");

        itemDto = new ItemDto();
        itemDto.setDescription("описание вещи");
        itemDto.setName("имя вещи");
        itemDto.setAvailable(true);
        itemDto.setId(44L);
        itemDto.setRequestId(22L);

        item1 = new Item();
        item1.setDescription("описание вещи");
        item1.setName("имя вещи");
        item1.setAvailable(true);
        item1.setId(44L);
        item1.setOwner(UserMapper.fromUserDto(ownerDto));

        item2 = new Item();
        item2.setDescription("2описание вещи");
        item2.setName("2имя вещи");
        item2.setAvailable(true);
        item2.setId(55L);
        item2.setOwner(UserMapper.fromUserDto(ownerDto));

        item3 = new Item();
        item3.setDescription("3описание вещи");
        item3.setName("3имя вещи");
        item3.setAvailable(true);
        item3.setId(66L);
        item3.setOwner(UserMapper.fromUserDto(ownerDto));

        bookingDto1 = new BookingDto();
        bookingDto1.setItemId(item1.getId());
        bookingDto1.setBookerId(bookerDto.getId());
        bookingDto1.setId(1L);
        bookingDto1.setStart(LocalDateTime.now().plusMinutes(1));
        bookingDto1.setEnd(LocalDateTime.now().plusDays(1));

        booking1 = new Booking();
        booking1.setId(1L);
        booking1.setItem(item1);
        booking1.setBooker(UserMapper.fromUserDto(bookerDto));
        booking1.setStart(bookingDto1.getStart());
        booking1.setEnd(bookingDto1.getEnd());

        bookingDto2 = new BookingDto();
        bookingDto1.setItemId(item2.getId());
        bookingDto1.setBookerId(bookerDto.getId());
        bookingDto2.setId(2L);
        bookingDto2.setStart(LocalDateTime.now().minusDays(3));
        bookingDto2.setEnd(LocalDateTime.now().minusDays(2));

        booking2 = new Booking();
        booking2.setId(2L);
        booking2.setItem(item2);
        booking2.setBooker(UserMapper.fromUserDto(bookerDto));
        booking2.setStart(bookingDto2.getStart());
        booking2.setEnd(bookingDto2.getEnd());

        bookingDto3 = new BookingDto();
        bookingDto1.setItemId(item3.getId());
        bookingDto1.setBookerId(bookerDto.getId());
        bookingDto3.setId(3L);
        bookingDto3.setStart(LocalDateTime.now().plusDays(4));
        bookingDto3.setEnd(LocalDateTime.now().plusDays(5));

        booking3 = new Booking();
        booking3.setId(3L);
        booking3.setItem(item3);
        booking3.setBooker(UserMapper.fromUserDto(bookerDto));
        booking3.setStart(bookingDto3.getStart());
        booking3.setEnd(bookingDto3.getEnd());

        bookingList = List.of(booking1, booking2, booking3);
    }

    @Test
    void createBookingTest() {
        // тест на создание бронирования
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(UserMapper.fromUserDto(ownerDto)));
        Mockito
                .when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item1));
        Mockito
                .when(mockBookingRepository.save(Mockito.any()))
                .thenReturn(booking1);

        Booking booking = bookingService.create(bookingDto1, bookerDto.getId());

        assertThat(booking.getId(), equalTo(bookingDto1.getId()));
        assertThat(booking.getItem().getId(), equalTo(item1.getId()));
        assertThat(booking.getItem().getOwner().getId(), equalTo(ownerDto.getId()));
        assertThat(booking.getBooker().getId(), equalTo(bookerDto.getId()));
    }

    @Test
    void getBookingByIdTest() {
        // тест на получение бронирования по Id
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(UserMapper.fromUserDto(ownerDto)));
        Mockito
                .when(mockBookingRepository.findById(booking1.getId()))
                .thenReturn(Optional.of(booking1));

        Booking booking = bookingService.get(ownerDto.getId(), booking1.getId());

        assertThat(booking.getId(), equalTo(booking1.getId()));
        assertThat(booking.getItem().getId(), equalTo(item1.getId()));
        assertThat(booking.getItem().getOwner().getId(), equalTo(ownerDto.getId()));
    }

    @Test
    void changeStatusBookingTest() {
        // тест на изменение статуса бронирования по Id
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(UserMapper.fromUserDto(ownerDto)));
        Mockito
                .when(mockBookingRepository.findById(booking1.getId()))
                .thenReturn(Optional.of(booking1));

        // тестируем на одобрение бронирования
        Booking booking = bookingService.changeStatus(ownerDto.getId(), booking1.getId(), true);

        assertThat(booking.getId(), equalTo(booking1.getId()));
        assertThat(booking.getItem().getId(), equalTo(item1.getId()));
        assertThat(booking.getItem().getOwner().getId(), equalTo(ownerDto.getId()));
        assertThat(booking.getStatus(), equalTo(BookingStatus.APPROVED));

        // тестируем на отклонение бронирования
        Mockito
                .when(mockBookingRepository.findById(booking2.getId()))
                .thenReturn(Optional.of(booking2));
        booking = bookingService.changeStatus(ownerDto.getId(), booking2.getId(), false);
        assertThat(booking.getStatus(), equalTo(BookingStatus.REJECTED));

    }

    @Test
    void getUserBookingsByStateTest() {
        // проверяем бронирования пользователя с разными статусами
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(UserMapper.fromUserDto(ownerDto)));
        Mockito
                .when(mockBookingRepository.findAllUserBookings(Mockito.anyLong(), Mockito.any()))
                .thenReturn(bookingList);
        Mockito
                .when(mockBookingRepository.findBookerCurrentBookings(Mockito.anyLong(), Mockito.any()))
                .thenReturn(bookingList);
        Mockito
                .when(mockBookingRepository.findBookerPastBookings(Mockito.anyLong(), Mockito.any()))
                .thenReturn(bookingList);
        Mockito
                .when(mockBookingRepository.findBookerFutureBookings(Mockito.anyLong(), Mockito.any()))
                .thenReturn(bookingList);
        Mockito
                .when(mockBookingRepository.findByBooker_IdAndStatusOrderByStartDesc(Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(bookingList);


        List<Booking> newBookingList = bookingService.getUserBookingsByState(bookerDto.getId(), "ALL", 1, 10);
        assertThat(newBookingList.size(), equalTo(bookingList.size()));
        assertThat(newBookingList.get(0).getId(), equalTo(bookingList.get(0).getId()));

        newBookingList = bookingService.getUserBookingsByState(bookerDto.getId(), "PAST", 1, 10);
        assertThat(newBookingList.size(), equalTo(bookingList.size()));
        assertThat(newBookingList.get(0).getId(), equalTo(bookingList.get(0).getId()));

        newBookingList = bookingService.getUserBookingsByState(bookerDto.getId(), "FUTURE", 1, 10);
        assertThat(newBookingList.size(), equalTo(bookingList.size()));
        assertThat(newBookingList.get(0).getId(), equalTo(bookingList.get(0).getId()));

        newBookingList = bookingService.getUserBookingsByState(bookerDto.getId(), "CURRENT", 1, 10);
        assertThat(newBookingList.size(), equalTo(bookingList.size()));
        assertThat(newBookingList.get(0).getId(), equalTo(bookingList.get(0).getId()));

        newBookingList = bookingService.getUserBookingsByState( bookerDto.getId(), "WAITING", 1, 10);
        assertThat(newBookingList.size(), equalTo(bookingList.size()));
        assertThat(newBookingList.get(0).getId(), equalTo(bookingList.get(0).getId()));

        newBookingList = bookingService.getUserBookingsByState(bookerDto.getId(), "REJECTED", 1, 10);
        assertThat(newBookingList.size(), equalTo(bookingList.size()));
        assertThat(newBookingList.get(0).getId(), equalTo(bookingList.get(0).getId()));
    }

    @Test
    void getOwnerBookingsByStateTest() {
        // проверяем бронирования владельца вещи с разными статусами
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(UserMapper.fromUserDto(ownerDto)));
        Mockito
                .when(mockBookingRepository.findByItem_Owner_IdOrderByStartDesc(Mockito.anyLong(), Mockito.any()))
                .thenReturn(bookingList);
        Mockito
                .when(mockBookingRepository.findOwnerCurrentBookings(Mockito.anyLong(), Mockito.any()))
                .thenReturn(bookingList);
        Mockito
                .when(mockBookingRepository.findOwnerPastBookings(Mockito.anyLong(), Mockito.any()))
                .thenReturn(bookingList);
        Mockito
                .when(mockBookingRepository.findOwnerFutureBookings(Mockito.anyLong(), Mockito.any()))
                .thenReturn(bookingList);
        Mockito
                .when(mockBookingRepository.findByItem_Owner_IdAndStatusOrderByStartDesc(Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(bookingList);


        List<Booking> newBookingList = bookingService.getOwnerBookingsByState(bookerDto.getId(), "ALL", 1, 10);
        assertThat(newBookingList.size(), equalTo(bookingList.size()));
        assertThat(newBookingList.get(0).getId(), equalTo(bookingList.get(0).getId()));

        newBookingList = bookingService.getOwnerBookingsByState(bookerDto.getId(), "PAST", 1, 10);
        assertThat(newBookingList.size(), equalTo(bookingList.size()));
        assertThat(newBookingList.get(0).getId(), equalTo(bookingList.get(0).getId()));

        newBookingList = bookingService.getOwnerBookingsByState(bookerDto.getId(), "FUTURE", 1, 10);
        assertThat(newBookingList.size(), equalTo(bookingList.size()));
        assertThat(newBookingList.get(0).getId(), equalTo(bookingList.get(0).getId()));

        newBookingList = bookingService.getOwnerBookingsByState(bookerDto.getId(), "CURRENT", 1, 10);
        assertThat(newBookingList.size(), equalTo(bookingList.size()));
        assertThat(newBookingList.get(0).getId(), equalTo(bookingList.get(0).getId()));

        newBookingList = bookingService.getOwnerBookingsByState( bookerDto.getId(), "WAITING", 1, 10);
        assertThat(newBookingList.size(), equalTo(bookingList.size()));
        assertThat(newBookingList.get(0).getId(), equalTo(bookingList.get(0).getId()));

        newBookingList = bookingService.getOwnerBookingsByState(bookerDto.getId(), "REJECTED", 1, 10);
        assertThat(newBookingList.size(), equalTo(bookingList.size()));
        assertThat(newBookingList.get(0).getId(), equalTo(bookingList.get(0).getId()));
    }
}
