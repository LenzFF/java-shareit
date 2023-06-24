package ru.practicum.shareit.request.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemRequestMapper {

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        ItemRequestDto itemRequestDto = new ItemRequestDto();

        itemRequestDto.setId(itemRequest.getId());
        itemRequestDto.setDescription(itemRequest.getDescription());
        itemRequestDto.setCreated(itemRequest.getCreated());
        itemRequestDto.setRequestor(UserMapper.toUserDto(itemRequest.getRequestor()));

        return itemRequestDto;
    }

    public static ItemRequest fromItemRequestDto(ItemRequestDto itemRequestDto) {
        ItemRequest itemRequest = new ItemRequest();

        itemRequest.setId(itemRequestDto.getId());
        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setCreated(itemRequestDto.getCreated());
        itemRequest.setRequestor(UserMapper.fromUserDto(itemRequestDto.getRequestor()));

        return itemRequest;
    }

    public static ItemRequestDtoWithAnswer toItemRequestDtoWithAnswer(ItemRequest itemRequest, List<ItemDto> answers) {
        ItemRequestDtoWithAnswer requestDto = new ItemRequestDtoWithAnswer();

        requestDto.setId(itemRequest.getId());
        requestDto.setDescription(itemRequest.getDescription());
        requestDto.setCreated(itemRequest.getCreated());
        requestDto.setRequestor(UserMapper.toUserDto(itemRequest.getRequestor()));

        if (answers != null) {
            requestDto.setItems(answers);
        }

        return requestDto;
    }
}
