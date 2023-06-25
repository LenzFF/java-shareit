package ru.practicum.shareit.integration;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithAnswer;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest()
public class ItemRequestServiceIntegrationTest {

    private final UserService userService;
    private final ItemService itemService;
    private final ItemRequestService requestService;
    public UserDto userDto1, userDto2;
    public ItemRequestDto itemRequestDto;
    public ItemDto itemDto1, itemDto2, itemDto3;

    @BeforeEach
    void setUp() {
        userDto1 = new UserDto();
        userDto1.setEmail("user1@yandex.ru");
        userDto1.setName("User1");

        userDto2 = new UserDto();
        userDto2.setEmail("user2@yandex.ru");
        userDto2.setName("User2");

        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("Description");

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
    }

    @Test
    void createRequestTest() {
        UserDto createdUserDto = userService.create(userDto1);

        ItemRequestDto createdRequestDto = requestService.create(itemRequestDto, createdUserDto.getId());

        assertThat(createdRequestDto.getDescription(), equalTo(itemRequestDto.getDescription()));
        assertThat(createdRequestDto.getRequestor().getId(), equalTo(createdUserDto.getId()));
    }

    @Test
    void getAllWithAnswersByUserIdTest() {
        UserDto createdUserDto = userService.create(userDto1);
        UserDto createdUserDto2 = userService.create(userDto2);

        ItemRequestDto createdRequestDto1 = requestService.create(itemRequestDto, createdUserDto.getId());
        ItemRequestDto createdRequestDto2 = requestService.create(itemRequestDto, createdUserDto.getId());
        ItemRequestDto createdRequestDto3 = requestService.create(itemRequestDto, createdUserDto.getId());

        itemDto1.setRequestId(createdRequestDto1.getId());
        itemDto2.setRequestId(createdRequestDto1.getId());
        itemDto3.setRequestId(createdRequestDto2.getId());

        itemService.create(createdUserDto2.getId(), itemDto1);
        itemService.create(createdUserDto2.getId(), itemDto2);
        itemService.create(createdUserDto2.getId(), itemDto3);

        List<ItemRequestDtoWithAnswer> requestDtoWithAnswerList = requestService
                .getAllItemRequestsWithAnswersByUserId(createdUserDto.getId());

        assertThat(requestDtoWithAnswerList.size(), equalTo(3));
        assertThat(requestDtoWithAnswerList.get(0).getId(), equalTo(createdRequestDto1.getId()));
        assertThat(requestDtoWithAnswerList.get(1).getId(), equalTo(createdRequestDto2.getId()));
        assertThat(requestDtoWithAnswerList.get(2).getId(), equalTo(createdRequestDto3.getId()));

        assertThat(requestDtoWithAnswerList.get(0).getItems().size(), equalTo(2));
        assertThat(requestDtoWithAnswerList.get(1).getItems().size(), equalTo(1));
        assertThat(requestDtoWithAnswerList.get(2).getItems().size(), equalTo(0));
    }
}
