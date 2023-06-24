package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithAnswer;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final UserRepository userStorage;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemStorage;

    private User getUserOrThrowException(long userId) {
        return userStorage.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Пользователь не найден, id - " + userId));
    }

    @Override
    public ItemRequestDto create(ItemRequestDto itemRequestDto, long userId) {
        itemRequestDto.setRequestor(UserMapper.toUserDto(getUserOrThrowException(userId)));

        return ItemRequestMapper.toItemRequestDto(itemRequestRepository
                .save(ItemRequestMapper.fromItemRequestDto(itemRequestDto)));
    }

    @Override
    public List<ItemRequestDtoWithAnswer> getAllItemRequestsWithAnswersByUserId(long userId) {
        getUserOrThrowException(userId);
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestorId(userId, Sort.by("created")
                .descending());

        return addAnswersToRequests(itemRequests);
    }

    @Override
    public List<ItemRequestDtoWithAnswer> getAllItemRequestsWithAnswers(long userId, int from, int size) {
        getUserOrThrowException(userId);

        if (from < 0 || size < 1) {
            throw new RuntimeException("Параметры для выборки должны быть: from >= 0, size > 0");
        }

        PageRequest page = PageRequest.of(from / size, size, Sort.by("created").descending());
        List<ItemRequest> requestList = itemRequestRepository.findAllByRequestorIdNot(userId, page);

        return addAnswersToRequests(requestList);
    }

    @Override
    public ItemRequestDtoWithAnswer getItemRequestWithAnswerById(long requestId, long userId) {
        getUserOrThrowException(userId);

        ItemRequest itemRequest = itemRequestRepository.findById(requestId);
        if (itemRequest == null) {
            throw new DataNotFoundException("Запрос не найден, id - " + requestId);
        }

        List<ItemRequestDtoWithAnswer> requestWithAnswrList = addAnswersToRequests(Collections.singletonList(itemRequest));
        return requestWithAnswrList.get(0);
    }


    private List<ItemRequestDtoWithAnswer> addAnswersToRequests(List<ItemRequest> itemRequestList) {

        List<ItemDto> itemDtoList = new ArrayList<>();

        if (!itemRequestList.isEmpty()) {
            List<Long> idList = itemRequestList.stream()
                    .map(ItemRequest::getId)
                    .collect(Collectors.toList());

            itemDtoList = itemStorage.findByRequestIdIn(idList)
                    .stream()
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
        }

        Map<Long, List<ItemDto>> itemsMap = itemDtoList.stream()
                .collect(Collectors.groupingBy(ItemDto::getRequestId));

        return itemRequestList.stream()
                .map(r -> ItemRequestMapper.toItemRequestDtoWithAnswer(r, itemsMap.get(r.getId())))
                .collect(Collectors.toList());
    }
}
