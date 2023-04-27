package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DataAlreadyExistException;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import java.util.List;


@Service
public class UserServiceImpl implements UserService {
    private UserStorage userStorage;

    @Autowired
    public UserServiceImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    private User getUserOrThrowException(long id) {
        User user = userStorage.get(id);
        if (user == null) {
            throw new DataNotFoundException("Пользователь не найден, id - " + id);
        }
        return user;
    }

    private void validateUser(User newUser) throws DataAlreadyExistException {
        if (newUser.getEmail() == null || !newUser.getEmail().contains("@")) {
            throw new ValidationException("Ошибка данных");
        }

        if (userStorage.getAll().stream().anyMatch(x -> x.getEmail().equals((newUser.getEmail())))) {
            throw new DataAlreadyExistException(newUser.getEmail() + " email уже используется");
        }
    }

    @Override
    public User create(User user) throws DataAlreadyExistException {
        validateUser(user);
        return userStorage.create(user);
    }

    @Override
    public List<User> getAll() {
        return userStorage.getAll();
    }

    @Override
    public User get(long id) {
        return getUserOrThrowException(id);
    }

    @Override
    public User update(long id, User updatedUser) throws DataAlreadyExistException {
        User user = getUserOrThrowException(id);

        if (updatedUser.getEmail() != null) {
            if (!user.getEmail().equals(updatedUser.getEmail()))
                validateUser(updatedUser);

            user.setEmail(updatedUser.getEmail());
        }

        if (updatedUser.getName() != null) user.setName(updatedUser.getName());

        return userStorage.update(user);
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
