package ru.practicum.shareit.user;

import ru.practicum.shareit.exception.DataAlreadyExistException;

import java.util.List;


public interface UserService {
    User create(User user) throws DataAlreadyExistException;

    List<User> getAll();

    User get(long id);

    User update(long id, User updatedUser) throws DataAlreadyExistException;

    void delete(long id);

    void deleteAll();
}
