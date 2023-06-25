package ru.practicum.shareit.jpa;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class ItemRepositoryTest {

    public UserDto userDto;
    public User user1, user2;
    public ItemRequest itemRequest;
    public ItemDto itemDto;
    public Item item1, item2;

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @BeforeEach
    public void setUp() {
        userDto = new UserDto();
        userDto.setEmail("user1@yandex.ru");
        userDto.setName("User1");

        user1 = new User();
        user1.setEmail("user1@yandex.ru");
        user1.setName("User1");

        user2 = new User();
        user2.setEmail("user2@yandex.ru");
        user2.setName("User2");

        itemRequest = new ItemRequest();
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setDescription("request Description");

        itemDto = new ItemDto();
        itemDto.setDescription("item description");
        itemDto.setName("item name");
        itemDto.setAvailable(true);
        itemDto.setId(33L);
        itemDto.setRequestId(22L);

        item1 = new Item();
        item1.setDescription("item description");
        item1.setName("item name");
        item1.setAvailable(true);
        item1.setOwner(user1);
        item1.setRequest(itemRequest);

        item2 = new Item();
        item2.setDescription("item description2");
        item2.setName("item name2");
        item2.setAvailable(true);
        item2.setOwner(user1);
        item2.setRequest(itemRequest);

        userRepository.save(user1);
        userRepository.save(user2);

        itemRequest.setRequestor(user2);
        itemRequestRepository.save(itemRequest);

        itemRepository.save(item1);
        itemRepository.save(item2);
    }

    @Test
    void findAllByOwnerId() {
        PageRequest page = PageRequest.of(0, 5);
        User owner = userRepository.findAll().get(0);

        List<Item> itemList = itemRepository.getByOwnerId(owner.getId(), page);

        assertThat(itemList.size(), equalTo(2));
        assertThat(itemList.get(0).getDescription(), equalTo(item1.getDescription()));
        assertThat(itemList.get(0).getName(), equalTo(item1.getName()));
        assertTrue(itemList.get(0).getAvailable());
    }

    @Test
    void searchItems() {
        PageRequest page = PageRequest.of(0, 5);
        User owner = userRepository.findAll().get(0);
        String searchRequest = "description2";

        List<Item> itemList = itemRepository.searchText(searchRequest, page);
        assertThat(itemList.size(), equalTo(1));
        assertTrue(itemList.get(0).getDescription().contains(searchRequest));
    }

    @Test
    void findByRequestIdIn() {
        ItemRequest request = itemRequestRepository.findAll().get(0);
        List<Long> requestIds = List.of(10L, 20L, 30L, request.getId());

        List<Item> itemList = itemRepository.findByRequestIdIn(requestIds);
        assertThat(itemList.size(), equalTo(2));
        assertThat(itemList.get(0).getRequest().getDescription(), equalTo(itemRequest.getDescription()));
        assertThat(itemList.get(1).getRequest().getDescription(), equalTo(itemRequest.getDescription()));
    }

    @AfterEach
    public void afterTest() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
        itemRequestRepository.deleteAll();
    }
}