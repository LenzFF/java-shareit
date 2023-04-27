package ru.practicum.shareit.user;

import lombok.*;

import javax.validation.constraints.Email;

/**
 * TODO Sprint add-controllers.
 */
@Getter
@Setter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class User {
    private Long id;
    @Email
    private String email;
    private String name;
}
