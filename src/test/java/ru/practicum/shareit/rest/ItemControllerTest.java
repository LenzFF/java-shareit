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
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {

    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemService itemService;
    public UserDto userDto1, userDto2;
    public ItemWithBookingsDto itemDtoWithBookings;
    public ItemDto itemDto;
    public CommentDto commentDto;


    @BeforeEach
    void setUp() {
        userDto1 = new UserDto();
        userDto1.setEmail("mail@mail.mail");
        userDto1.setName("myName");

        userDto2 = new UserDto();
        userDto2.setEmail("2mail@mail.mail");
        userDto2.setName("2myName");

        itemDtoWithBookings = new ItemWithBookingsDto();
        itemDtoWithBookings.setDescription("description");
        itemDtoWithBookings.setName("itemname");
        itemDtoWithBookings.setAvailable(true);
        itemDtoWithBookings.setId(2);
        itemDtoWithBookings.setLastBooking(new BookingDto());

        itemDto = new ItemDto();
        itemDto.setDescription("description2");
        itemDto.setName("itemname2");
        itemDto.setAvailable(true);
        itemDto.setId(2);

        commentDto = new CommentDto();
        commentDto.setAuthorName("author");
        commentDto.setText("text text");
    }

    @Test
    @SneakyThrows
    void postItemTest() {
        // создание новой вещи
        long ownerId = 1L;
        Mockito
                .when(itemService.create(Mockito.anyLong(), Mockito.any()))
                .thenReturn(itemDto);

        String result = mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", ownerId)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Mockito.verify(itemService).create(Mockito.anyLong(), Mockito.any());
        assertThat(mapper.writeValueAsString(itemDto), equalTo(result));
    }

    @Test
    @SneakyThrows
    void patchItemTest() {
        // обновление вещи
        long itemId = 2L, ownerId = 1L;
        Mockito
                .when(itemService.update(Mockito.anyLong(), Mockito.any()))
                .thenReturn(itemDto);

        String result = mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", ownerId)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Mockito.verify(itemService).update(Mockito.anyLong(), Mockito.any());
        assertThat(mapper.writeValueAsString(itemDto), equalTo(result));
    }

    @Test
    @SneakyThrows
    void getItemByIdTest() {
        //получение вещи по id
        long itemId = 2L, ownerId = 1L;

        Mockito
                .when(itemService.get(ownerId, itemId))
                .thenReturn(itemDtoWithBookings);

        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoWithBookings.getId()), long.class))
                .andExpect(jsonPath("$.name", is(itemDtoWithBookings.getName())))
                .andExpect(jsonPath("$.available", is(itemDtoWithBookings.getAvailable())));

        Mockito.verify(itemService).get(ownerId, itemId);
    }

    @Test
    @SneakyThrows
    void getAllItemsByUserTest() {
        // получение всех вещей пользователя
        long ownerId = 1;
        int from = 0;
        int size = 2;

        List<ItemWithBookingsDto> itemDtoListWithBookings = List.of(new ItemWithBookingsDto(), new ItemWithBookingsDto());

        Mockito.when(itemService.getAllUserItems(ownerId, from, size))
                .thenReturn(itemDtoListWithBookings);

        String result = mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", ownerId)
                        .queryParam("from", "0")
                        .queryParam("size", "2"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Mockito.verify(itemService).getAllUserItems(ownerId, from, size);
        assertThat(mapper.writeValueAsString(itemDtoListWithBookings), equalTo(result));
    }

    @Test
    @SneakyThrows
    void searchItemsTest() {
        // поиск вещей
        long ownerId = 1;
        int from = 0;
        int size = 2;
        String text = "search";

        List<ItemDto> itemDtoList = List.of(new ItemDto(), new ItemDto());

        Mockito.when(itemService.searchItem(ownerId, text, from, size))
                .thenReturn(itemDtoList);

        String result = mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", ownerId)
                        .queryParam("text", "search")
                        .queryParam("from", "0")
                        .queryParam("size", "2"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Mockito.verify(itemService).searchItem(ownerId, text, from, size);
        assertThat(mapper.writeValueAsString(itemDtoList), equalTo(result));
    }

    @Test
    @SneakyThrows
    void createCommentTest() {
        // создание комментария
        long authorId = 1;
        long itemId = 2;
        Mockito
                .when(itemService.createComment(Mockito.anyLong(), Mockito.anyLong(), Mockito.any()))
                .thenReturn(commentDto);

        String result = mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", authorId)
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) //проверяем статус 200
                .andExpect(jsonPath("$.id", is(commentDto.getId()), long.class))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Mockito.verify(itemService).createComment(Mockito.anyLong(), Mockito.anyLong(), Mockito.any());
        assertThat(mapper.writeValueAsString(commentDto), equalTo(result));
    }
}
