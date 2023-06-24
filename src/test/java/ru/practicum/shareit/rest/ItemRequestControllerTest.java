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
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithAnswer;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {

    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    public ItemRequestService itemRequestService;
    public ItemRequestDto itemRequestDto1, itemRequestDto2;
    public ItemRequestDtoWithAnswer itemRequestDtoWithAnswer;
    public UserDto userDto1;
    public Item item;
    public ItemDto itemDto;

    @BeforeEach
    void setUp() {
        userDto1 = new UserDto();
        userDto1.setEmail("mail@mail.mail");
        userDto1.setName("myName");

        item = new Item(11L, "staff1", "super staff", true);
        itemDto = ItemMapper.toItemDto(item);

        itemRequestDto1 = new ItemRequestDto();
        itemRequestDto1.setId(1L);
        itemRequestDto1.setDescription("need staff1");
        itemRequestDto1.setRequestor(userDto1);

        itemRequestDto2 = new ItemRequestDto();
        itemRequestDto2.setId(2L);
        itemRequestDto2.setDescription("need staff2");
        itemRequestDto2.setRequestor(userDto1);

        itemRequestDtoWithAnswer = new ItemRequestDtoWithAnswer();
        itemRequestDtoWithAnswer.setId(itemRequestDto1.getId());
        itemRequestDtoWithAnswer.setDescription(itemRequestDto1.getDescription());
        itemRequestDtoWithAnswer.setCreated(itemRequestDto1.getCreated());
        itemRequestDtoWithAnswer.setRequestor(itemRequestDto1.getRequestor());
        itemRequestDtoWithAnswer.setItems(List.of(itemDto));
    }

    @Test
    @SneakyThrows
    void postItemRequestTest() {
        // создание нового запроса
        long userId = 1L;

        Mockito
                .when(itemRequestService.create(Mockito.any(), Mockito.anyLong()))
                .thenReturn(itemRequestDto1);

        String result = mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(itemRequestDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto1.getId()), long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto1.getDescription())))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Mockito.verify(itemRequestService).create(Mockito.any(), Mockito.anyLong());
        assertThat(mapper.writeValueAsString(itemRequestDto1), equalTo(result));
    }

    @Test
    @SneakyThrows
    void getItemRequestByIdTest() {
        //получение запроса по id
        long requestId = 2L, userId = 1L;

        Mockito
                .when(itemRequestService.getItemRequestWithAnswerById(requestId, userId))
                .thenReturn(itemRequestDtoWithAnswer);

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDtoWithAnswer.getId()), long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDtoWithAnswer.getDescription())));

        Mockito.verify(itemRequestService).getItemRequestWithAnswerById(requestId, userId);
    }

    @Test
    @SneakyThrows
    void getAllUserRequestsTest() {
        // получение всех запросов пользователя
        long userId = 1L;
        int from = 0;
        int size = 10;

        List<ItemRequestDtoWithAnswer> list = List.of(new ItemRequestDtoWithAnswer(), new ItemRequestDtoWithAnswer());


        Mockito.when(itemRequestService.getAllItemRequestsWithAnswers(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(list);

        String result = mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId)
                        .queryParam("from", "0")
                        .queryParam("size", "10"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Mockito.verify(itemRequestService).getAllItemRequestsWithAnswers(userId, from, size);
        assertThat(mapper.writeValueAsString(list), equalTo(result));
    }

    @Test
    @SneakyThrows
    void getRequestsWithAnswersTest() {
        // получение всех запросов на вещь
        long userId = 1L;

        List<ItemRequestDtoWithAnswer> list = List.of(new ItemRequestDtoWithAnswer(), new ItemRequestDtoWithAnswer());


        Mockito.when(itemRequestService.getAllItemRequestsWithAnswersByUserId(Mockito.anyLong()))
                .thenReturn(list);

        String result = mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Mockito.verify(itemRequestService).getAllItemRequestsWithAnswersByUserId(userId);
        assertThat(mapper.writeValueAsString(list), equalTo(result));
    }
}
