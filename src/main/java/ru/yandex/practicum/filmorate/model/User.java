package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@Builder
public class User {
    private int id;
    @Email
    @NotBlank(message = "Электронная почта не может быть пустой")
    private String email;
    @NotBlank(message = "Логин не может быть пустым и содержать пробелы")
    private String login;
    private String name;
    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;
    private Set<Integer> friends;

    this.friends =new HashSet<>();

}
