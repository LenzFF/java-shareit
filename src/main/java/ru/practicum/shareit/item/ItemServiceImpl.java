package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;


    @Override
    public List<ItemDto> getAllUserItems(long userId) {
        userStorage.get(userId)
                .orElseThrow(() -> new DataNotFoundException("Пользователь не найден, id - " + userId));

        return itemStorage.getAll().stream()
                .filter(x -> x.getOwner().getId() == userId)
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto get(long userId, long itemId) {
        return ItemMapper.toItemDto(getItemOrThrowException(userId, itemId));
    }


    @Override
    public ItemDto create(long userId, ItemDto itemDto) {
        User user = userStorage.get(userId)
                .orElseThrow(() -> new DataNotFoundException("Пользователь не найден, id - " + userId));

        Item item = ItemMapper.fromItemDto(itemDto);
        item.setOwner(user);
        return ItemMapper.toItemDto(itemStorage.create(item));
    }

    @Override
    public ItemDto update(long userId, ItemDto itemDto) {
        Item item = getItemOrThrowException(userId, itemDto.getId());

        if (item.getOwner().getId() != userId) {
            throw new DataNotFoundException("Вещь не найдена, id - " + item.getId());
        }

        if (itemDto.getName() != null && !itemDto.getName().isBlank())
            item.setName(itemDto.getName());

        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank())
            item.setDescription(itemDto.getDescription());

        if (itemDto.getAvailable() != null)
            item.setAvailable(itemDto.getAvailable());

        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> searchItem(long userId, String text) {
        if (text.isBlank()) return Collections.EMPTY_LIST;

        String str = text.toLowerCase();

        return itemStorage.getAll().stream()
                .map(ItemMapper::toItemDto)
                .filter(x -> x.getName().toLowerCase().contains(str)
                        || x.getDescription().toLowerCase().contains(str)
                        && x.getAvailable())
                .collect(Collectors.toList());
    }

    private Item getItemOrThrowException(long userId, long itemId) {
        userStorage.get(userId)
                .orElseThrow(() -> new DataNotFoundException("Пользователь не найден, id - " + userId));

        return itemStorage.get(itemId)
                .orElseThrow(() -> new DataNotFoundException("Вещь не найдена, itemId - " + itemId));
    }
}
