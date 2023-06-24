package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
public class ItemRequestDtoWithAnswer extends ItemRequestDto {

    private List<ItemDto> items = new ArrayList<>();
}
