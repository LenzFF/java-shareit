package ru.practicum.shareit.jpa;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest
class ItemRequestRepositoryTest {

    private User user1, user2;
    private ItemRequest itemRequest1, itemRequest2;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setEmail("user1@yandex.ru");
        user1.setName("User1");

        user2 = new User();
        user2.setEmail("user2@yandex.ru");
        user2.setName("User2");

        itemRequest1 = new ItemRequest();
        itemRequest1.setCreated(LocalDateTime.now());
        itemRequest1.setDescription("Description1");

        itemRequest2 = new ItemRequest();
        itemRequest2.setCreated(LocalDateTime.now());
        itemRequest2.setDescription("Description2");

        userRepository.save(user1);
        userRepository.save(user2);

        itemRequest1.setRequestor(user1);
        itemRequest2.setRequestor(user2);

        itemRequestRepository.save(itemRequest1);
        itemRequestRepository.save(itemRequest2);
    }

    @AfterEach
    void tearDown() {
        itemRequestRepository.deleteAll();
        userRepository.deleteAll();
    }


    @Test
    void findAllByRequestorId() {
        List<ItemRequest> requestList = itemRequestRepository.findAllByRequestorId(user1.getId(),
                Sort.by("created").descending());

        assertThat(requestList.size(), equalTo(1));
        assertThat(requestList.get(0).getDescription(), equalTo(itemRequest1.getDescription()));
    }

    @Test
    void findAllByRequestor_IdNot() {
        PageRequest page = PageRequest.of(0, 1, Sort.by("created").descending());

        List<ItemRequest> requestList = itemRequestRepository.findAllByRequestorIdNot(user1.getId(), page);

        assertThat(requestList.size(), equalTo(1));
        assertThat(requestList.get(0).getDescription(), equalTo(itemRequest2.getDescription()));
    }
}
