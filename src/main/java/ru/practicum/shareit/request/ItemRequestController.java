package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithAnswer;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto createRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                        @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.create(itemRequestDto, userId);
    }

    @GetMapping
    public List<ItemRequestDtoWithAnswer> getRequestsWithAnswers(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestService.getAllItemRequestsWithAnswersByUserId(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoWithAnswer> getAllRequests(@RequestHeader("X-Sharer-User-Id") long userId,
                                                         @RequestParam(name = "from", defaultValue = "0") int from,
                                                         @RequestParam(name = "size", defaultValue = "3") int size) {
        return itemRequestService.getAllItemRequestsWithAnswers(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoWithAnswer getRequestById(@RequestHeader("X-Sharer-User-Id") long userId,
                                                   @PathVariable long requestId) {
        return itemRequestService.getItemRequestWithAnswerById(requestId, userId);
    }

}
