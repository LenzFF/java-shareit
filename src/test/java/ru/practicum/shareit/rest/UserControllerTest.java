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
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {

    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;

    public UserDto userDto1, userDto2;

    public ItemWithBookingsDto itemDtoWithBookings;
    public ItemDto itemDto;
    public CommentDto commentDto;


    @BeforeEach
    void setUp() {
        /*создаем тестовые объекты*/
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
    void getUserTest() {
        // получение пользователя по id
        long userId = 2;

        Mockito.when(userService.get(userId))
                .thenReturn(userDto1);

        mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(userDto1.getName())))
                .andExpect(jsonPath("$.email", is(userDto1.getEmail())));

        Mockito.verify(userService).get(userId);
    }

    @Test
    @SneakyThrows
    void getAllUsersTest() {
        // получение всех пользователя
        List<UserDto> userDtos = List.of(userDto1, userDto2);

        Mockito.when(userService.getAll())
                .thenReturn(userDtos);

        String result = mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Mockito.verify(userService).getAll();
        assertThat(mapper.writeValueAsString(userDtos), equalTo(result));
    }

    @Test
    @SneakyThrows
    void postUserTest() {
        // создание нового пользователя
        Mockito
                .when(userService.create(Mockito.any()))
                .thenReturn(userDto1);

        String result = mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) //проверяем статус 200
                .andExpect(jsonPath("$.name", is(userDto1.getName())))
                .andExpect(jsonPath("$.email", is(userDto1.getEmail())))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Mockito.verify(userService).create(Mockito.any());
        assertThat(mapper.writeValueAsString(userDto1), equalTo(result));
    }

    @Test
    @SneakyThrows
    void deleteUserTest() {
        // удаление пользователя
        int userId = 2;

        mockMvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isOk());

        Mockito.verify(userService).delete(userId);
    }

    @Test
    @SneakyThrows
    void updateUserTest() {
        // обновление пользователя
        long userId = 2;
        Mockito
                .when(userService.update(Mockito.anyLong(), Mockito.any()))
                .thenReturn(userDto1);
        Mockito
                .when(userService.get(Mockito.anyLong()))
                .thenReturn(userDto1);


        String result = mockMvc.perform(patch("/users/{userId}", userId)
                        .content(mapper.writeValueAsString(userDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) //проверяем статус 200
                .andExpect(jsonPath("$.name", is(userDto1.getName())))
                .andExpect(jsonPath("$.email", is(userDto1.getEmail())))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Mockito.verify(userService).update(Mockito.anyLong(), Mockito.any());
        assertThat(mapper.writeValueAsString(userDto1), equalTo(result));
    }
}
