package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithAnswer;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto create(ItemRequestDto itemRequestDto, long userId);

    List<ItemRequestDtoWithAnswer> getAllItemRequestsWithAnswersByUserId(long userId);

    List<ItemRequestDtoWithAnswer> getAllItemRequestsWithAnswers(long userId, int from, int size);

    ItemRequestDtoWithAnswer getItemRequestWithAnswerById(long requestId, long userId);
}
