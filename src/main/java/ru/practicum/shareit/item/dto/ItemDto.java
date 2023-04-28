package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.user.dto.Marker;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class ItemDto {
    private long id;

    @NotBlank(groups = {Marker.OnCreate.class})
    private String name;

    @NotBlank(groups = {Marker.OnCreate.class})
    private String description;

    @NotNull(groups = {Marker.OnCreate.class})
    private Boolean available;
}