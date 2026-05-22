package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
@Validated
public class FilmController {

    private final Map<Integer, Film> films = new HashMap<>();
    private int nextFilmId = 1;

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Получен запрос на получение всех фильмов");
        return films.values();
    }

    @PostMapping
    public Film add(@Valid @RequestBody Film film) {
        if (film == null) {
            throw new ValidationException("Тело запроса не может быть null");
        }
        Film savedFilm = saveFilm(film);
        log.info("Добавлен новый фильм: {}", savedFilm);
        return savedFilm;
    }

    private Film saveFilm(Film film) {
        assignId(film);
        storeFilm(film);
        return film;
    }

    private void assignId(Film film) {
        film.setId(nextFilmId++);
    }

    private void storeFilm(Film film) {
        films.put(film.getId(), film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info("Получен PUT запрос на обновление фильма с id={} (из тела запроса)", film.getId());

        Integer id = film.getId();

        // Проверяем, что ID не null
        if (id == null) {
            throw new ValidationException("ID фильма не может быть null");
        }

        // Проверяем, существует ли фильм с таким id
        if (!films.containsKey(id)) {
            log.warn("Фильм с id={} не найден", id);
            throw new ValidationException("Фильм с id=" + id + " не найден");
        }

        // Опциональная проверка: обновлять только если есть изменения
        Film existingFilm = films.get(id);
        boolean hasChanges = !existingFilm.equals(film);

        if (hasChanges) {
            films.put(id, film);
            log.info("Фильм с id={} был обновлён: {}", id, film);
        } else {
            log.info("Фильм с id={} не изменился", id);
        }

        // Возвращаем обновлённый фильм
        return film;
    }
}
