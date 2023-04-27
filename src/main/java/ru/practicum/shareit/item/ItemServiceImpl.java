package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    private ItemStorage itemStorage;
    private UserService userService;

    @Autowired
    public ItemServiceImpl(ItemStorage itemStorage, UserService userService) {
        this.itemStorage = itemStorage;
        this.userService = userService;
    }

    private Item getItemOrThrowException(long userId, long itemId) {
        userService.get(userId);
        Item item = itemStorage.get(itemId);

        if (item == null) {
            throw new DataNotFoundException("Вещь не найдена, itemId - " + itemId);
        }

        return item;
    }


    @Override
    public List<ItemDto> getAllUserItems(long userId) {
        userService.get(userId);

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
        User user = userService.get(userId);
        validation(itemDto);
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

        if (itemDto.getName() != null) item.setName(itemDto.getName());
        if (itemDto.getDescription() != null) item.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null) item.setAvailable(itemDto.getAvailable());

        return ItemMapper.toItemDto(itemStorage.update(item));
    }

    @Override
    public List<ItemDto> searchItem(long userId, String text) {
        String str = text.toLowerCase();

        if (text.isEmpty()) return Collections.EMPTY_LIST;

        return itemStorage.getAll().stream()
                .map(ItemMapper::toItemDto)
                .filter(x -> x.getName().toLowerCase().contains(str)
                        || x.getDescription().toLowerCase().contains(str)
                        && x.getAvailable())
                .collect(Collectors.toList());
    }

    private void validation(ItemDto itemDto) {
        if (itemDto.getName() == null ||
                itemDto.getDescription() == null ||
                itemDto.getName().isBlank() ||
                itemDto.getAvailable() == null) {
            throw new ValidationException("Ошибка ввода");
        }
    }
}
