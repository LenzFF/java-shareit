package ru.practicum.shareit.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
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
public class ItemServiceTest {

    public UserDto userDto;
    public ItemRequest itemRequest;
    public ItemDto itemDto;
    public Item item1, item2, item3;
    public Booking booking1, booking2, booking3;
    public Comment comment1;
    public CommentDto commentDto;
    public List<Item> itemList;
    public List<Booking> bookingList;
    public List<Comment> commentList;
    @Mock
    static UserRepository userRepository;
    @Mock
    static ItemRepository mockItemRepository;
    @Mock
    static BookingRepository mockBookingRepository;
    @Mock
    static CommentRepository mockCommentRepository;
    @Mock
    static ItemRequestRepository mockItemRequestRepository;

    @InjectMocks
    private ItemServiceImpl itemService1;

    @BeforeEach
    void setUp() {
        userDto = new UserDto();
        userDto.setId(11L);
        userDto.setEmail("mail@mail.mail");
        userDto.setName("myName");

        itemRequest = new ItemRequest();
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setDescription("request Description");
        itemRequest.setId(22L);

        itemDto = new ItemDto();
        itemDto.setDescription("описание вещи");
        itemDto.setName("имя вещи");
        itemDto.setAvailable(true);
        itemDto.setId(33L);
        itemDto.setRequestId(22L);

        item1 = new Item();
        item1.setDescription("описание вещи");
        item1.setName("имя вещи");
        item1.setAvailable(true);
        item1.setId(33L);
        item1.setOwner(UserMapper.fromUserDto(userDto));

        item2 = new Item();
        item2.setDescription("2описание вещи");
        item2.setName("2имя вещи");
        item2.setAvailable(true);
        item2.setId(55L);
        item2.setOwner(UserMapper.fromUserDto(userDto));

        item3 = new Item();
        item3.setDescription("3описание вещи");
        item3.setName("3имя вещи");
        item3.setAvailable(true);
        item3.setId(66L);
        item3.setOwner(UserMapper.fromUserDto(userDto));

        booking1 = new Booking();
        booking1.setItem(item1);
        booking1.setBooker(UserMapper.fromUserDto(userDto));
        booking1.setId(1L);
        booking1.setStatus(BookingStatus.APPROVED);
        booking1.setStart(LocalDateTime.now());
        booking1.setEnd(LocalDateTime.now().plusDays(1));

        booking2 = new Booking();
        booking2.setItem(item2);
        booking2.setBooker(UserMapper.fromUserDto(userDto));
        booking2.setId(2L);
        booking2.setStatus(BookingStatus.APPROVED);
        booking2.setStart(LocalDateTime.now().plusDays(2));
        booking2.setEnd(LocalDateTime.now().plusDays(3));

        booking3 = new Booking();
        booking3.setItem(item3);
        booking3.setBooker(UserMapper.fromUserDto(userDto));
        booking3.setId(3L);
        booking3.setStatus(BookingStatus.APPROVED);
        booking3.setStart(LocalDateTime.now().plusDays(4));
        booking3.setEnd(LocalDateTime.now().plusDays(5));

        comment1 = new Comment();
        comment1.setItem(item1);
        comment1.setText("комментарий 1");
        comment1.setAuthor(UserMapper.fromUserDto(userDto));
        comment1.setId(1);
        comment1.setCreated(LocalDateTime.now());

        commentDto = new CommentDto();
        commentDto.setAuthorName("author");

        itemList = List.of(item1, item2, item3);
        bookingList = List.of(booking1, booking2, booking3);
        commentList = List.of(comment1);
    }

    @Test
    void createItemTest() {
        Mockito
                .when(userRepository.findById(11L))
                .thenReturn(Optional.of(UserMapper.fromUserDto(userDto)));
        Mockito
                .when(mockItemRepository.save(Mockito.any()))
                .thenReturn(ItemMapper.fromItemDto(itemDto));
        Mockito
                .when(mockItemRequestRepository.findById(22L))
                .thenReturn(itemRequest);

        ItemDto newItemDto = itemService1.create(11L, itemDto);

        assertThat(newItemDto.getName(), equalTo("имя вещи"));
        assertThat(newItemDto.getId(), equalTo(33L));
    }

    @Test
    void getItemDtoByIdTest() {
        // получение вещи по id
        Mockito
                .when(userRepository.findById(11L))
                .thenReturn(Optional.of(UserMapper.fromUserDto(userDto)));
        Mockito
                .when(mockItemRepository.findById(33L))
                .thenReturn(Optional.of(item1));

        ItemDto newItemDto = itemService1.get(11L, 33L);

        assertThat(newItemDto.getName(), equalTo("имя вещи"));
        assertThat(newItemDto.getDescription(), equalTo("описание вещи"));
        assertThat(newItemDto.getId(), equalTo(33L));
    }

    @Test
    void getAllItemsDtoByUserTest() {
        // получаем список всех вещей
        Mockito
                .when(userRepository.findById(11L))
                .thenReturn(Optional.of(UserMapper.fromUserDto(userDto)));
        Mockito
                .when(mockItemRepository.getByOwnerId(Mockito.anyLong(), Mockito.any()))
                .thenReturn(itemList);
        Mockito
                .when(mockBookingRepository.findItemLastBookings(Mockito.anyLong()))
                .thenReturn(bookingList);
        Mockito
                .when(mockBookingRepository.findItemNextBookings(Mockito.anyLong()))
                .thenReturn(bookingList);


        List<ItemWithBookingsDto> itemDtoList = itemService1.getAllUserItems(11L, 1, 10);

        assertThat(itemDtoList.size(), equalTo(3));
        assertThat(itemDtoList.get(0).getId(), equalTo(itemList.get(0).getId()));
        assertThat(itemDtoList.get(1).getId(), equalTo(itemList.get(1).getId()));
        assertThat(itemDtoList.get(2).getId(), equalTo(itemList.get(2).getId()));
    }

    @Test
    void updateItemTest() {
        //тестируем обновление вещи
        Mockito
                .when(userRepository.findById(11L))
                .thenReturn(Optional.of(UserMapper.fromUserDto(userDto)));
        Mockito
                .when(mockItemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item1));
        Mockito
                .when(mockItemRepository.save(Mockito.any()))
                .thenReturn(item1);

        ItemDto updatedItemDto = itemService1.update(11L, itemDto);

        Mockito.verify(mockItemRepository, Mockito.times(1))
                .findById(item1.getId());
        Mockito.verify(mockItemRepository, Mockito.times(1))
                .save(Mockito.any());

        assertThat(updatedItemDto.getId(), equalTo(item1.getId()));
    }

    @Test
    void searchItemsDtoTest() {

        Mockito
                .when(mockItemRepository.searchText(Mockito.anyString(), Mockito.any()))
                .thenReturn(itemList);

        List<ItemDto> itemsDto = itemService1.searchItem(11L, "aaa", 1, 10);

        Mockito.verify(mockItemRepository, Mockito.times(1))
                .searchText(Mockito.anyString(), Mockito.any());

        assertThat(itemsDto.get(0).getId(), equalTo(itemList.get(0).getId()));
        assertThat(itemsDto.get(1).getId(), equalTo(itemList.get(1).getId()));
        assertThat(itemsDto.get(2).getId(), equalTo(itemList.get(2).getId()));
    }

    @Test
    void createCommentTest() {
        // создаем комментарий
        Mockito
                .when(userRepository.findById(11L))
                .thenReturn(Optional.of(UserMapper.fromUserDto(userDto)));
        Mockito
                .when(mockItemRepository.findById(33L))
                .thenReturn(Optional.of(item1));
        Mockito
                .when(mockCommentRepository.save(Mockito.any()))
                .thenReturn(comment1);
        Mockito
                .when(mockBookingRepository.findBookerItemBookings(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(bookingList);

        CommentDto newCommentDto = itemService1.createComment(11L, 33L, commentDto);

        assertThat(newCommentDto.getId(), equalTo(commentDto.getId()));
    }
}

