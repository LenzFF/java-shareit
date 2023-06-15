package ru.practicum.shareit.booking.dto;

import lombok.*;
import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
public class BookingDto {
    private long id;

    @NotNull
    private long itemId;

    private long bookerId;

    @NotNull
    @Future
    private LocalDateTime start;

    @NotNull
    @Future
    private LocalDateTime end;
}
