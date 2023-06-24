package ru.practicum.shareit.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    public UserDto userDto1, userDto2, userDto3;
    public User user;
    public List<UserDto> userDtoList;
    public List<User> userList;
    @Mock
    static UserRepository mockUserStorage;

    @InjectMocks
    public UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userDto1 = new UserDto();
        userDto1.setId(11L);
        userDto1.setEmail("11mail@mail.mail");
        userDto1.setName("11myName");

        userDto2 = new UserDto();
        userDto2.setId(22L);
        userDto2.setEmail("22mail@mail.mail");
        userDto2.setName("22myName");

        userDto3 = new UserDto();
        userDto3.setId(33L);
        userDto3.setEmail("33mail@mail.mail");
        userDto3.setName("33myName");

        user = new User();
        user.setId(33L);
        user.setEmail("33mail@mail.mail");
        user.setName("44myName");

        userDtoList = Arrays.asList(userDto1, userDto2, userDto3);
        userList = userDtoList.stream()
                .map(UserMapper::fromUserDto)
                .collect(Collectors.toList());
    }

    @Test
    void createAndUpdateUserTest() {
        Mockito
                .when(mockUserStorage.save(Mockito.any()))
                .thenReturn(UserMapper.fromUserDto(userDto1));

        UserDto newUserDto = userService.create(userDto1);

        assertThat(newUserDto.getId(), equalTo(userDto1.getId()));
        assertThat(newUserDto.getEmail(), equalTo(userDto1.getEmail()));

        Mockito
                .when(mockUserStorage.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(UserMapper.fromUserDto(userDto3)));
        Mockito
                .when(mockUserStorage.save(Mockito.any()))
                .thenReturn(UserMapper.fromUserDto(userDto3));


        UserDto updatedUserDto = userService.update(33L, userDto3);

        Mockito.verify(mockUserStorage, Mockito.times(1))
                .findById(userDto3.getId());
        Mockito.verify(mockUserStorage, Mockito.times(2))
                .save(Mockito.any());

        assertThat(updatedUserDto.getId(), equalTo(user.getId()));
    }

    @Test
    void getUserDtoByIdTest() {
        Mockito
                .when(mockUserStorage.findById(11L))
                .thenReturn(Optional.of(UserMapper.fromUserDto(userDto1)));

        UserDto newUserDto = userService.get(11);

        assertThat(newUserDto.getId(), equalTo(userDto1.getId()));
        assertThat(newUserDto.getEmail(), equalTo(userDto1.getEmail()));
    }

    @Test
    void getAllDtoTest() {
        Mockito
                .when(mockUserStorage.findAll())
                .thenReturn(userList);

        List<UserDto> newUserDtolist = userService.getAll();

        assertThat(newUserDtolist.size(), equalTo(userList.size()));
        assertThat(newUserDtolist.get(0).getId(), equalTo(userList.get(0).getId()));
        assertThat(newUserDtolist.get(1).getId(), equalTo(userList.get(1).getId()));
        assertThat(newUserDtolist.get(2).getId(), equalTo(userList.get(2).getId()));
    }
}
