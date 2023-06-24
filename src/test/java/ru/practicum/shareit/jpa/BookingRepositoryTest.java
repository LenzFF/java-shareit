package ru.practicum.shareit.jpa;

import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest
class BookingRepositoryTest {

    private User user1, user2;
    private Item item1, item2;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private BookingRepository bookingRepository;
    private Booking booking1, booking2, booking3;

    @BeforeEach
    public void setUp() {
        user1 = new User();
        user1.setEmail("user1@yandex.ru");
        user1.setName("User1");

        user2 = new User();
        user2.setEmail("user2@yandex.ru");
        user2.setName("User2");

        userRepository.save(user1);
        userRepository.save(user2);

        item1 = new Item();
        item1.setDescription("Description1");
        item1.setName("Item1");
        item1.setAvailable(true);
        item1.setOwner(user1);

        item2 = new Item();
        item2.setDescription("Description2");
        item2.setName("Item2");
        item2.setAvailable(true);
        item2.setOwner(user2);

        itemRepository.save(item1);
        itemRepository.save(item2);

        //запрос не от собственника
        booking1 = new Booking();
        booking1.setItem(item1);
        booking1.setStart(LocalDateTime.now());
        booking1.setEnd(LocalDateTime.now().plusSeconds(1));
        booking1.setBooker(user2);

        booking2 = new Booking();
        booking2.setItem(item2);
        booking2.setStart(LocalDateTime.now());
        booking2.setEnd(LocalDateTime.now().plusSeconds(2));
        booking2.setBooker(user1);

        booking3 = new Booking();
        booking3.setItem(item2);
        booking3.setStart(LocalDateTime.now().plusSeconds(2));
        booking3.setEnd(LocalDateTime.now().plusSeconds(5));
        booking3.setBooker(user1);
        booking3.setStatus(BookingStatus.REJECTED);

        bookingRepository.save(booking1);
        bookingRepository.save(booking2);
        bookingRepository.save(booking3);
    }

    @AfterEach
    public void afterTest() {
        userRepository.deleteAll();
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
    }

    @Test
    void findByOwner() {
        PageRequest page = PageRequest.of(0, 5);

        List<Booking> bookingList = bookingRepository.findByItem_Owner_IdOrderByStartDesc(user1.getId(), page);

        assertThat(bookingList.size(), equalTo(1));
        assertThat(bookingList.get(0).getItem().getName(), equalTo(item1.getName()));
    }

    @Test
    void findByOwnerAndStatus() {
        PageRequest page = PageRequest.of(0, 5);

        List<Booking> bookingList = bookingRepository.findByItem_Owner_IdAndStatusOrderByStartDesc(user2.getId(),
                BookingStatus.REJECTED, page);

        assertThat(bookingList.size(), equalTo(1));
        assertThat(bookingList.get(0).getItem().getName(), equalTo(item2.getName()));
        assertThat(bookingList.get(0).getStatus(), equalTo(booking3.getStatus()));
    }

    @Test
    @SneakyThrows
    void findPastByOwner() {
        PageRequest page = PageRequest.of(0, 5);

        TimeUnit.SECONDS.sleep(3);
        List<Booking> bookingList = bookingRepository.findOwnerPastBookings(user2.getId(), page);

        assertThat(bookingList.size(), equalTo(1));
        assertThat(bookingList.get(0).getItem().getName(), equalTo(item2.getName()));
        assertThat(bookingList.get(0).getId(), equalTo(booking2.getId()));
    }

    @Test
    @SneakyThrows
    void findFutureByOwner() {
        PageRequest page = PageRequest.of(0, 5);
        List<Booking> bookingList = bookingRepository.findOwnerFutureBookings(user2.getId(), page);

        assertThat(bookingList.size(), equalTo(1));
        assertThat(bookingList.get(0).getId(), equalTo(booking3.getId()));
    }

    @Test
    @SneakyThrows
    void findCurrentByOwner() {
        PageRequest page = PageRequest.of(0, 5);

        TimeUnit.SECONDS.sleep(3);
        List<Booking> bookingList = bookingRepository.findOwnerCurrentBookings(user2.getId(), page);

        assertThat(bookingList.size(), equalTo(1));
        assertThat(bookingList.get(0).getId(), equalTo(booking3.getId()));
    }

    @Test
    void findByBooker() {
        PageRequest page = PageRequest.of(0, 5);

        List<Booking> bookingList = bookingRepository.findAllUserBookings(user1.getId(), page);

        assertThat(bookingList.size(), equalTo(2));
        assertThat(bookingList.get(0).getItem().getName(), equalTo(item2.getName()));
    }

    @Test
    void findByBookerAndStatus() {
        PageRequest page = PageRequest.of(0, 5);

        List<Booking> bookingList = bookingRepository.findByBooker_IdAndStatusOrderByStartDesc(user1.getId(),
                BookingStatus.REJECTED, page);

        assertThat(bookingList.size(), equalTo(1));
        assertThat(bookingList.get(0).getItem().getName(), equalTo(item2.getName()));
        assertThat(bookingList.get(0).getStatus(), equalTo(booking3.getStatus()));
    }

    @Test
    @SneakyThrows
    void findPastByBooker() {
        PageRequest page = PageRequest.of(0, 5);

        TimeUnit.SECONDS.sleep(3);
        List<Booking> bookingList = bookingRepository.findBookerPastBookings(user2.getId(), page);

        assertThat(bookingList.size(), equalTo(1));
        assertThat(bookingList.get(0).getItem().getName(), equalTo(item1.getName()));
    }

    @Test
    @SneakyThrows
    void findFutureByBooker() {
        PageRequest page = PageRequest.of(0, 5);
        List<Booking> bookingList = bookingRepository.findBookerFutureBookings(user2.getId(), page);

        assertThat(bookingList.size(), equalTo(0));
    }

    @Test
    @SneakyThrows
    void findCurrentByBooker() {
        PageRequest page = PageRequest.of(0, 5);

        TimeUnit.SECONDS.sleep(3);
        List<Booking> bookingList = bookingRepository.findBookerCurrentBookings(user2.getId(), page);

        assertThat(bookingList.size(), equalTo(0));
    }
}