package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ItemStorageImpl implements ItemStorage {

    private long id = 0;
    private final Map<Long, Item> itemStorage = new HashMap<>();

    @Override
    public List<Item> getAll() {
        return new ArrayList<>(itemStorage.values());
    }

    @Override
    public Item get(long id) {
        return itemStorage.get(id);
    }

    @Override
    public void delete(long id) {
        itemStorage.remove(id);
    }

    @Override
    public void deleteAll() {
        id = 0;
        itemStorage.clear();
    }

    @Override
    public Item create(Item item) {
        item.setId(++id);
        itemStorage.put(item.getId(), item);
        return itemStorage.get(id);
    }

    @Override
    public Item update(Item item) {
        itemStorage.replace(item.getId(), item);
        return itemStorage.get(item.getId());
    }
}
