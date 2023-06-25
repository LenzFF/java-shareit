package ru.practicum.shareit.user.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class UserDto {
    private Long id;

    @NotEmpty(groups = {Marker.OnCreate.class}, message = "Email не может быть пустым")
    @Email(groups = {Marker.OnCreate.class, Marker.OnUpdate.class}, message = "Email не верный")
    private String email;

    @NotBlank(groups = {Marker.OnCreate.class})
    private String name;
}