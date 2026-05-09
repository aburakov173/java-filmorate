package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.MinReleaseDate;

import java.time.LocalDate;

@Data
public class Film {
    private int id;

    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;

    @Size(max = 200, message = "Описание не должно превышать 200 символов")
    private String description;

    @Positive(message = "Продолжительность должна быть положительным числом")
    private int duration;

    @PastOrPresent(message = "Дата релиза не может быть в будущем")
    @MinReleaseDate(message = "Дата релиза не может быть раньше 28 декабря 1895 года")
    @NotNull(message = "Дата релиза обязательна")
    private LocalDate releaseDate;
}
