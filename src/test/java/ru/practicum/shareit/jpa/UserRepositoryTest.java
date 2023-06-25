package ru.practicum.shareit.jpa;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest
class UserRepositoryTest {

    public User user1, user2;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setEmail("user1@yandex.ru");
        user1.setName("User1");

        user2 = new User();
        user2.setEmail("user2@yandex.ru");
        user2.setName("User2");

        userRepository.save(user1);
        userRepository.save(user2);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void getUsersEmails() {
        List<String> emails = userRepository.findAll()
                .stream()
                .map(User::getEmail)
                .collect(Collectors.toList());

        assertThat(emails.size(), equalTo(2));
        assertThat(emails.get(0), equalTo(user1.getEmail()));
        assertThat(emails.get(1), equalTo(user2.getEmail()));
    }
}
