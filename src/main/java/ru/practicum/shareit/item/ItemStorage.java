package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemStorage {
    List<Item> getAll();

    Optional<Item> get(long id);

    void delete(long id);

    void deleteAll();

    Item create(Item item);

    Item update(Item item);
}
