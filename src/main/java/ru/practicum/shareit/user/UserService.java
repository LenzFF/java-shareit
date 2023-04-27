package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto create(UserDto userDto);

    List<UserDto> getAll();

    UserDto get(long id);

    UserDto update(long id, UserDto updatedUserDto);

    void delete(long id);

    void deleteAll();
}
