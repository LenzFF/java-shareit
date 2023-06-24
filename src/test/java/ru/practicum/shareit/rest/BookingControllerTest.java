package ru.practicum.shareit.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {

    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BookingService bookingService;
    public UserDto userDto1, userDto2;
    public Item item;
    public ItemWithBookingsDto itemWithBookingsDto;
    public ItemDto itemDto;
    public CommentDto commentDto;
    private BookingDto bookingDto;
    private Booking booking;


    @BeforeEach
    void setUp() {
        userDto1 = new UserDto();
        userDto1.setEmail("mail@mail.mail");
        userDto1.setName("myName");

        userDto2 = new UserDto();
        userDto2.setEmail("2mail@mail.mail");
        userDto2.setName("2myName");

        itemWithBookingsDto = new ItemWithBookingsDto();
        itemWithBookingsDto.setDescription("description");
        itemWithBookingsDto.setName("itemname");
        itemWithBookingsDto.setAvailable(true);
        itemWithBookingsDto.setId(2);
        itemWithBookingsDto.setLastBooking(new BookingDto());

        itemDto = new ItemDto();
        itemDto.setDescription("description2");
        itemDto.setName("itemname2");
        itemDto.setAvailable(true);
        itemDto.setId(2L);

        item = ItemMapper.fromItemDto(itemDto);

        commentDto = new CommentDto();
        commentDto.setAuthorName("author");
        commentDto.setText("text text");

        bookingDto = new BookingDto();
        bookingDto.setId(33L);
        bookingDto.setStart(LocalDateTime.now().plusSeconds(1));
        bookingDto.setEnd(LocalDateTime.now().plusSeconds(2));

        booking = new Booking();
        booking.setStatus(BookingStatus.WAITING);
        booking.setId(33L);
        booking.setStart(LocalDateTime.now().plusSeconds(1));
        booking.setEnd(LocalDateTime.now().plusSeconds(2));
        booking.setItem(item);
    }

    @Test
    @SneakyThrows
    void postBookingTest() {
        long ownerId = 1L;

        Mockito
                .when(bookingService.create(Mockito.any(), Mockito.anyLong()))
                .thenReturn(booking);

        String result = mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", ownerId)
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId()), long.class))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Mockito.verify(bookingService).create(Mockito.any(), Mockito.anyLong());
        assertThat(mapper.writeValueAsString(booking), equalTo(result));
    }

    @Test
    @SneakyThrows
    void changeStatusBookingTest() {
        // меняем статус бронирования
        long bookingId = 2L;
        long userId = 1L;
        boolean isApproved = true;

        Mockito
                .when(bookingService.changeStatus(userId, bookingId, isApproved))
                .thenReturn(booking);

        String result = mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .queryParam("approved", "true")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId()), long.class))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Mockito.verify(bookingService).changeStatus(userId, bookingId, isApproved);
        assertThat(mapper.writeValueAsString(booking), equalTo(result));
    }

    @Test
    @SneakyThrows
    void getBookingTest() {
        // запрашиваем бронирование по id
        long bookingId = 2L;
        long userId = 1L;

        Mockito.when(bookingService.get(userId, bookingId))
                .thenReturn(booking);

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId()), long.class));

        Mockito.verify(bookingService).get(userId, bookingId);
    }

    @Test
    @SneakyThrows
    void getAllByUserTest() {
        // запрашиваем все бронировая пользователя
        long userId = 1L;
        int from = 0;
        int size = 2;
        String state = "WAITING";

        List<Booking> bookings = List.of(new Booking(), new Booking());

        Mockito.when(bookingService.getUserBookingsByState(userId, state, from, size))
                .thenReturn(bookings);

        String result = mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .queryParam("state", state)
                        .queryParam("from", "0")
                        .queryParam("size", "2"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Mockito.verify(bookingService).getUserBookingsByState(userId, state, from, size);
        assertThat(mapper.writeValueAsString(bookings), equalTo(result));
    }

    @Test
    @SneakyThrows
    void getAllByItemTest() {
        // запрашиваем все бронировая конкретной вещи
        long userId = 1L;
        int from = 0;
        int size = 2;
        String state = "WAITING";

        List<Booking> bookings = List.of(new Booking(), new Booking());

        Mockito.when(bookingService.getOwnerBookingsByState(userId, state, from, size))
                .thenReturn(bookings);

        String result = mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .queryParam("state", state)
                        .queryParam("from", "0")
                        .queryParam("size", "2"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Mockito.verify(bookingService).getOwnerBookingsByState(userId, state, from, size);
        assertThat(mapper.writeValueAsString(bookings), equalTo(result));
    }
}
