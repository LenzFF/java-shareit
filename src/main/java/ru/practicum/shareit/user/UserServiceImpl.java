package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DataAlreadyExistException;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userStorage;

    @Override
    public UserDto create(UserDto userDto) {
        User user = userStorage.save(UserMapper.fromUserDto(userDto));
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAll() {
        return userStorage.findAll()
                .stream()
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
            if (!user.getEmail().equals(updatedUserDto.getEmail())) {
                if (userStorage.findByEmail(updatedUserDto.getEmail()).size() > 0) {
                    throw new DataAlreadyExistException("Email уже используется");
                }
                user.setEmail(updatedUserDto.getEmail());
            }
        }

        if (updatedUserDto.getName() != null && !updatedUserDto.getName().isBlank()) {
            user.setName(updatedUserDto.getName());
        }

        userStorage.save(user);

        return UserMapper.toUserDto(user);
    }

    @Override
    public void delete(long id) {
        getUserOrThrowException(id);
        userStorage.deleteById(id);
    }

    @Override
    public void deleteAll() {
        userStorage.deleteAll();
    }

    private User getUserOrThrowException(long id) {
        return userStorage.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Пользователь не найден, id - " + id));
    }
}
