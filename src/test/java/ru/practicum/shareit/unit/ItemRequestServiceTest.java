package ru.practicum.shareit.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.ItemRequestServiceImpl;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithAnswer;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceTest {

    public UserDto userDto;
    public ItemRequest itemRequest1, itemRequest2, itemRequest3;
    public ItemRequestDto itemRequestDto;
    public Item item1, item2, item3;
    public List<Item> itemList;
    public List<ItemRequest> requestList;
    @Mock
    static UserRepository userRepository;
    @Mock
    static ItemRepository mockItemRepository;
    @Mock
    static ItemRequestRepository mockItemRequestRepository;
    @InjectMocks
    public ItemRequestServiceImpl requestService;


    @BeforeEach
    void setUp() {
        userDto = new UserDto();
        userDto.setId(11L);
        userDto.setEmail("mail@mail.mail");
        userDto.setName("myName");

        itemRequest1 = new ItemRequest();
        itemRequest1.setCreated(LocalDateTime.now());
        itemRequest1.setDescription("request Description");
        itemRequest1.setId(22L);
        itemRequest1.setRequestor(UserMapper.fromUserDto(userDto));

        itemRequest2 = new ItemRequest();
        itemRequest2.setCreated(LocalDateTime.now());
        itemRequest2.setDescription("request Description");
        itemRequest2.setId(88L);
        itemRequest2.setRequestor(UserMapper.fromUserDto(userDto));

        itemRequest3 = new ItemRequest();
        itemRequest3.setCreated(LocalDateTime.now());
        itemRequest3.setDescription("request Description");
        itemRequest3.setId(99L);
        itemRequest3.setRequestor(UserMapper.fromUserDto(userDto));

        itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest1);

        item1 = new Item();
        item1.setDescription("описание вещи");
        item1.setName("имя вещи");
        item1.setAvailable(true);
        item1.setId(44L);
        item1.setOwner(UserMapper.fromUserDto(userDto));
        item1.setRequest(itemRequest1);

        item2 = new Item();
        item2.setDescription("2описание вещи");
        item2.setName("2имя вещи");
        item2.setAvailable(true);
        item2.setId(55L);
        item2.setOwner(UserMapper.fromUserDto(userDto));
        item2.setRequest(itemRequest2);

        item3 = new Item();
        item3.setDescription("3описание вещи");
        item3.setName("3имя вещи");
        item3.setAvailable(true);
        item3.setId(66L);
        item3.setOwner(UserMapper.fromUserDto(userDto));
        item3.setRequest(itemRequest3);

        itemList = List.of(item1, item2, item3);
        requestList = List.of(itemRequest1, itemRequest2, itemRequest3); // список запросов
    }

    @Test
    void createRequestTest() {
        // тест создания запроса на вещь
        Mockito
                .when(userRepository.findById(11L))
                .thenReturn(Optional.of(UserMapper.fromUserDto(userDto)));
        Mockito
                .when(mockItemRequestRepository.save(Mockito.any()))
                .thenReturn(itemRequest1);

        ItemRequestDto newRequestDto = requestService.create(itemRequestDto, 11L);

        assertThat(newRequestDto.getId(), equalTo(itemRequest1.getId()));
        assertThat(newRequestDto.getRequestor().getId(), equalTo(userDto.getId()));
    }

    @Test
    void getAllItemRequestsDtoByUserIdWithAnswersTest() {
        // получение всех запросов от одного пользователя
        Mockito
                .when(userRepository.findById(11L))
                .thenReturn(Optional.of(UserMapper.fromUserDto(userDto)));
        Mockito
                .when(mockItemRequestRepository.findAllByRequestorId(11L, Sort.by("created").descending()))
                .thenReturn(requestList);
        Mockito
                .when(mockItemRepository.findByRequestIdIn(Mockito.anyList()))
                .thenReturn(itemList);

        List<ItemRequestDtoWithAnswer> newRequestDtoList = requestService.getAllItemRequestsWithAnswersByUserId(11L);

        assertThat(newRequestDtoList.size(), equalTo(requestList.size()));
        assertThat(newRequestDtoList.get(0).getId(), equalTo(requestList.get(0).getId()));
    }

    @Test
    void getAllItemRequestsDtoWithAnswersTest() {
        // получение всех запросов по одной вещи
        Mockito
                .when(userRepository.findById(22L))
                .thenReturn(Optional.of(UserMapper.fromUserDto(userDto)));
        Mockito
                .when(mockItemRequestRepository.findAllByRequestorIdNot(Mockito.anyLong(), Mockito.any()))
                .thenReturn(requestList);
        Mockito
                .when(mockItemRepository.findByRequestIdIn(Mockito.anyList()))
                .thenReturn(itemList);

        List<ItemRequestDtoWithAnswer> newRequestDtoList = requestService.getAllItemRequestsWithAnswers(22L, 1, 2);

        assertThat(newRequestDtoList.size(), equalTo(requestList.size()));
        assertThat(newRequestDtoList.get(0).getId(), equalTo(requestList.get(0).getId()));
    }

    @Test
    void getItemRequestsDtoWithAnswersByIdTest() {
        //тест получения запроса по id
        Mockito
                .when(userRepository.findById(11L))
                .thenReturn(Optional.of(UserMapper.fromUserDto(userDto)));
        Mockito
                .when(mockItemRequestRepository.findById(22L))
                .thenReturn(itemRequest1);
        Mockito
                .when(mockItemRepository.findByRequestIdIn(Mockito.anyList()))
                .thenReturn(itemList);

        ItemRequestDtoWithAnswer newRequestDto = requestService.getItemRequestWithAnswerById(22L, 11L);

        assertThat(newRequestDto.getId(), equalTo(itemRequest1.getId()));
    }
}
