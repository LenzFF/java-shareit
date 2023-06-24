package ru.practicum.shareit.integration;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest()
public class ItemServiceIntegrationTest {

    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;
    public static UserDto userDto1, userDto2;
    public ItemDto itemDto1;
    private BookingDto bookingDto;
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

        bookingDto = new BookingDto();

        commentDto = new CommentDto();
        commentDto.setText("comment1");
    }


    @Test
    @SneakyThrows
    void bigItemsTest() {
        UserDto owner = userService.create(userDto1);
        UserDto booker = userService.create(userDto2);

        //тест на создание
        ItemDto newItemDto = itemService.create(owner.getId(), itemDto1);

        assertThat(newItemDto.getName(), equalTo(itemDto1.getName()));
        assertThrows(DataNotFoundException.class,
                () -> itemService.create(100L, itemDto1));

        //создаем бронирование
        bookingDto.setItemId(newItemDto.getId());
        bookingDto.setStart(LocalDateTime.now());
        bookingDto.setEnd(LocalDateTime.now().plusSeconds(2));

        Booking booking = bookingService.create(bookingDto, booker.getId());
        bookingService.changeStatus(owner.getId(), booking.getId(), true);

        //создаем комментарий
        TimeUnit.SECONDS.sleep(3);
        CommentDto comment = itemService.createComment(booker.getId(), newItemDto.getId(), commentDto);

        //проверка на бронирование и комментарий
        ItemWithBookingsDto itemDtoWithBookings = itemService.get(owner.getId(), newItemDto.getId());

        assertThat(itemDtoWithBookings.getName(), equalTo(itemDto1.getName()));
        assertThat(itemDtoWithBookings.getLastBooking().getId(), equalTo(booking.getId()));
        assertThat(itemDtoWithBookings.getComments().size(), equalTo(1));
        assertThat(itemDtoWithBookings.getComments().get(0).getId(), equalTo(comment.getId()));

        assertThat(itemDtoWithBookings.getComments().get(0).getId(), equalTo(comment.getId()));
        assertThat(itemDtoWithBookings.getComments().get(0).getText(), equalTo(comment.getText()));

        //ошибка при запросе с неверным id
        Assertions.assertThrows(DataNotFoundException.class,
                () -> itemService.get(1L, 11L));

        //проверка получения вещи
        newItemDto = itemService.get(owner.getId(), newItemDto.getId());

        assertThat(newItemDto.getName(), equalTo(itemDto1.getName()));

        //проверка обновления вещи
        newItemDto.setName("updated name");

        itemService.update(owner.getId(), newItemDto);

        ItemDto updatedItemDto = itemService.get(owner.getId(), newItemDto.getId());

        MatcherAssert.assertThat(updatedItemDto.getName(), equalTo("updated name"));

        //проверка обновления несуществующей вещи
        ItemDto finalNewItemDto = newItemDto;
        Assertions.assertThrows(DataNotFoundException.class,
                () -> itemService.update(100L, finalNewItemDto));


        itemService.create(owner.getId(), itemDto1);
        itemService.create(owner.getId(), itemDto2);
        itemService.create(owner.getId(), itemDto3);

        //проверка получения всех вещей пользователя
        List<ItemWithBookingsDto> itemDtoWithBookingsList = itemService.getAllUserItems(owner.getId(), 0, 10);

        assertThat(itemDtoWithBookingsList.size(), equalTo(4));

        //проверка пагинации
        Assertions.assertThrows(RuntimeException.class,
                () -> itemService.getAllUserItems(1L, -1, 2));
        Assertions.assertThrows(RuntimeException.class,
                () -> itemService.getAllUserItems(1L, 0, -1));


        itemService.create(owner.getId(), itemDto1);
        itemService.create(owner.getId(), itemDto2);
        itemService.create(owner.getId(), itemDto3);

        //тест на поиск
        List<ItemDto> itemDtoList = itemService.searchItem(2L, "поиск", 0, 10);

        assertThat(itemDtoList.size(), equalTo(5));
        assertTrue(itemDtoList.get(0).getDescription().contains("поиск"));
        assertTrue(itemDtoList.get(1).getDescription().contains("поиск"));
        assertTrue(itemDtoList.get(2).getDescription().contains("поиск"));
        assertTrue(itemDtoList.get(3).getDescription().contains("поиск"));
        assertTrue(itemDtoList.get(4).getDescription().contains("поиск"));
    }
}
