package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
public class ItemRequestDto {

    private long id;

    @NotBlank
    private String description;

    private UserDto requestor;

    private LocalDateTime created = LocalDateTime.now();
}