package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DataAlreadyExistException;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserServiceImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    private User getUserOrThrowException(long id) {
        return userStorage.get(id)
                .orElseThrow(() -> new DataNotFoundException("Пользователь не найден, id - " + id));
    }

    private void checkEmail(UserDto userDto) {
        if (userStorage.getAll().stream().anyMatch(x -> x.getEmail().equals((userDto.getEmail())))) {
            throw new DataAlreadyExistException(userDto.getEmail() + " email уже используется");
        }
    }

    @Override
    public UserDto create(UserDto userDto) {
        checkEmail(userDto);
        User user = userStorage.create(UserMapper.fromUserDto(userDto));
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAll() {
        return userStorage.getAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto get(long id) {
        return UserMapper.toUserDto(getUserOrThrowException(id));
    }

    @Override
    public UserDto update(long id, UserDto updatedUserDto) {
        User user = getUserOrThrowException(id);

        if (updatedUserDto.getEmail() != null && !updatedUserDto.getEmail().isBlank()) {
            if (!user.getEmail().equals(updatedUserDto.getEmail()))
                checkEmail(updatedUserDto);

            user.setEmail(updatedUserDto.getEmail());
        }

        if (updatedUserDto.getName() != null && !updatedUserDto.getName().isBlank()) {
            user.setName(updatedUserDto.getName());
        }

        return UserMapper.toUserDto(user);
    }

    @Override
    public void delete(long id) {
        getUserOrThrowException(id);
        userStorage.delete(id);
    }

    @Override
    public void deleteAll() {
        userStorage.deleteAll();
    }
}
