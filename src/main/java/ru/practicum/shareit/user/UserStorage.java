package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    User create(User user);

    List<User> getAll();

    Optional<User> get(long id);

    User update(User user);

    void delete(long id);

    void deleteAll();

}
