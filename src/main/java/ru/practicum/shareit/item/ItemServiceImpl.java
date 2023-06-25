package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemStorage;
    private final UserRepository userStorage;
    private final BookingRepository bookingStorage;
    private final CommentRepository commentStorage;
    private final ItemRequestRepository itemRequestStorage;


    @Override
    public List<ItemWithBookingsDto> getAllUserItems(long userId, int from, int size) {
        if (from < 0 || size < 1) {
            throw new RuntimeException("Параметры для выборки должны быть: from >= 0, size > 0");
        }

        PageRequest page = PageRequest.of(from / size, size);

        getUserOrThrowException(userId);

        List<ItemWithBookingsDto> items = itemStorage.getByOwnerId(userId, page).stream()
                .map(this::toItemDtoWithBookings)
                .collect(Collectors.toList());

        items.forEach(this::addBookings);


        return items;
    }

    private ItemWithBookingsDto toItemDtoWithBookings(Item item) {
        ItemWithBookingsDto itemWithBookingsDto = new ItemWithBookingsDto();
        itemWithBookingsDto.setId(item.getId());
        itemWithBookingsDto.setAvailable(item.getAvailable());
        itemWithBookingsDto.setName(item.getName());
        itemWithBookingsDto.setDescription(item.getDescription());
        itemWithBookingsDto.setComments(commentStorage.findByItemId(item.getId())
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList()));


        if (item.getRequest() != null) itemWithBookingsDto.setRequestId(item.getRequest().getId());

        return itemWithBookingsDto;
    }

    private void addBookings(ItemWithBookingsDto item) {
        List<Booking> bookings = bookingStorage.findItemLastBookings(item.getId());
        BookingDto lastBookingDto = new BookingDto();
        if (bookings.size() > 0) {
            lastBookingDto.setId(bookings.get(bookings.size() - 1).getId());
            lastBookingDto.setBookerId(bookings.get(bookings.size() - 1).getBooker().getId());
            item.setLastBooking(lastBookingDto);
        }

        bookings = bookingStorage.findItemNextBookings(item.getId());
        BookingDto nextBookingDto = new BookingDto();
        if (bookings.size() > 0) {
            nextBookingDto.setId(bookings.get(0).getId());
            nextBookingDto.setBookerId(bookings.get(0).getBooker().getId());
            item.setNextBooking(nextBookingDto);
        }
    }

    private User getUserOrThrowException(long userId) {
        return userStorage.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Пользователь не найден, id - " + userId));
    }

    @Override
    public ItemWithBookingsDto get(long userId, long itemId) {
        Item item = getItemOrThrowException(userId, itemId);
        ItemWithBookingsDto itemWithBookingsDto = this.toItemDtoWithBookings(item);

        if (item.getOwner().getId() == userId) {
            addBookings(itemWithBookingsDto);
        }

        return itemWithBookingsDto;
    }


    @Transactional
    @Override
    public ItemDto create(long userId, ItemDto itemDto) {
        User user = getUserOrThrowException(userId);

        Item item = ItemMapper.fromItemDto(itemDto);
        item.setOwner(user);

        if (itemDto.getRequestId() != 0) {
            item.setRequest(itemRequestStorage.findById(itemDto.getRequestId()));
        }

        return ItemMapper.toItemDto(itemStorage.save(item));
    }

    @Transactional
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

        itemStorage.save(item);

        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> searchItem(long userId, String text, int from, int size) {
        if (from < 0 || size < 1) {
            throw new RuntimeException("Параметры для выборки должны быть: from >= 0, size > 0");
        }

        PageRequest page = PageRequest.of(from / size, size);

        if (text.isBlank()) return Collections.EMPTY_LIST;

        return itemStorage.searchText(text, page)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public CommentDto createComment(long userId, long itemId, CommentDto commentDto) {
        Comment comment = CommentMapper.fromCommentDto(commentDto);
        comment.setAuthor(getUserOrThrowException(userId));
        comment.setItem(getItemOrThrowException(userId, itemId));

        if (bookingStorage.findBookerItemBookings(itemId, userId).isEmpty()) {
            throw new ValidationException("Бронирования не найдены");
        }

        commentStorage.save(comment);

        return CommentMapper.toCommentDto(comment);
    }

    private Item getItemOrThrowException(long userId, long itemId) {
        getUserOrThrowException(userId);

        return itemStorage.findById(itemId)
                .orElseThrow(() -> new DataNotFoundException("Вещь не найдена, itemId - " + itemId));
    }
}
