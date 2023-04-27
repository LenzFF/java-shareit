package ru.practicum.shareit.user;

import java.util.List;

public interface UserStorage {
    User create(User user);

    List<User> getAll();

    User get(long id);

    User update(User user);

    void delete(long id);

    void deleteAll();

}
