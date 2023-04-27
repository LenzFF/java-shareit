package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class UserStorageImpl implements UserStorage {

    private long id = 0;
    private final Map<Long, User> userStorage = new HashMap<>();

    @Override
    public User create(User user) {
        user.setId(++id);
        userStorage.put(id, user);
        return userStorage.get(id);
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(userStorage.values());
    }

    @Override
    public User get(long id) {
        return userStorage.get(id);
    }

    @Override
    public User update(User user) {
        userStorage.replace(user.getId(), user);
        return userStorage.get(user.getId());
    }

    @Override
    public void delete(long id) {
        userStorage.remove(id);
    }

    @Override
    public void deleteAll() {
        id = 0;
        userStorage.clear();
    }
}
