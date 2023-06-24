package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.user.dto.Marker;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") long userId,
                              @Validated(Marker.OnCreate.class) @RequestBody ItemDto itemDto) {
        return itemService.create(userId, itemDto);
    }

    @PatchMapping("/{id}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long id,
                              @RequestBody ItemDto itemDto) {
        itemDto.setId(id);
        return itemService.update(userId, itemDto);
    }

    @GetMapping
    public List<ItemWithBookingsDto> getAllUserItems(@RequestHeader("X-Sharer-User-Id") long userId,
                                                     @RequestParam(name = "from", defaultValue = "0") int from,
                                                     @RequestParam(name = "size", defaultValue = "3") int size) {
        return itemService.getAllUserItems(userId, from, size);
    }

    @GetMapping("/{id}")
    public ItemWithBookingsDto getItemById(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @PathVariable long id) {
        return itemService.get(userId, id);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                    @RequestParam(value = "text") String text,
                                    @RequestParam(name = "from", defaultValue = "0") int from,
                                    @RequestParam(name = "size", defaultValue = "3") int size) {
        return itemService.searchItem(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId,
                                    @RequestBody @Valid CommentDto commentDto) {
        return itemService.createComment(userId, itemId, commentDto);
    }
}
