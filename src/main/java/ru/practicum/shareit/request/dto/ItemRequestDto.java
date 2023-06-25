package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
public class ItemRequestDto {

    private long id;

    @NotBlank
    @Size(max = 1000)
    private String description;

    private UserDto requestor;

    private LocalDateTime created = LocalDateTime.now();
}