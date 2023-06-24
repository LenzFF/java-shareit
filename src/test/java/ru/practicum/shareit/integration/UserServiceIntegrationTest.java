package ru.practicum.shareit.integration;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DataAlreadyExistException;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest()
public class UserServiceIntegrationTest {

    private final UserService userService;
    public UserDto userDto1, userDto2;

    @BeforeEach
    void setUp() {
        userDto1 = new UserDto();
        userDto1.setEmail("user1@yandex.ru");
        userDto1.setName("User1");

        userDto2 = new UserDto();
        userDto2.setEmail("user2@yandex.ru");
        userDto2.setName("User2");
    }

    @Test
    void createUserTest() {
        UserDto createdUserDto = userService.create(userDto1);

        assertThat(createdUserDto.getName(), equalTo(userDto1.getName()));
        assertThat(createdUserDto.getEmail(), equalTo(userDto1.getEmail()));
    }

    @Test
    void getUserDtoByIdTest() {
        UserDto createdUserDto = userService.create(userDto1);

        UserDto newUserDto = userService.get(createdUserDto.getId());

        assertThat(newUserDto.getName(), equalTo(createdUserDto.getName()));
        assertThat(newUserDto.getEmail(), equalTo(createdUserDto.getEmail()));
        assertThat(newUserDto.getId(), equalTo(createdUserDto.getId()));

        assertThrows(DataNotFoundException.class,
                () -> userService.get(100L));
    }

    @Test
    void getAllDtoTest() {
        UserDto createdUserDto = userService.create(userDto1);
        UserDto createdUserDto2 = userService.create(userDto2);

        List<UserDto> newUserDtoList = userService.getAll();

        assertThat(newUserDtoList.size(), equalTo(2));
        assertThat(newUserDtoList.get(0).getId(), equalTo(createdUserDto.getId()));
        assertThat(newUserDtoList.get(1).getId(), equalTo(createdUserDto2.getId()));
    }

    @Test
    void deleteUserTest() {
        UserDto createdUserDto = userService.create(userDto1);

        userService.delete(createdUserDto.getId());

        assertThrows(DataNotFoundException.class,
                () -> userService.get(createdUserDto.getId()));
    }

    @Test
    void updateItemTest() {
        UserDto createdUserDto = userService.create(userDto1);
        userService.create(userDto2);

        createdUserDto.setName("Updated");

        userService.update(createdUserDto.getId(), createdUserDto);

        UserDto updatedUserDto = userService.get(createdUserDto.getId());

        assertThat(updatedUserDto.getName(), equalTo("Updated"));

        assertThrows(DataNotFoundException.class,
                () -> userService.update(100L, createdUserDto));

        createdUserDto.setEmail("user2@yandex.ru");
        assertThrows(DataAlreadyExistException.class,
                () -> userService.update(createdUserDto.getId(), createdUserDto));
    }
}