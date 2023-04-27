package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    List<Item> getAll();

    Item get(long id);

    void delete(long id);

    void deleteAll();

    Item create(Item item);

    Item update(Item item);
}
