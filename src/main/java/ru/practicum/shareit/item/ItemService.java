package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;

import java.util.List;

public interface ItemService {
    List<ItemWithBookingsDto> getAllUserItems(long userId);

    ItemWithBookingsDto get(long userId, long itemId);

    ItemDto create(long userId, ItemDto itemDto);

    ItemDto update(long userId, ItemDto itemDto);

    List<ItemDto> searchItem(long userId, String text);

    CommentDto createComment(long userId, long itemId, CommentDto commentDto);
}

