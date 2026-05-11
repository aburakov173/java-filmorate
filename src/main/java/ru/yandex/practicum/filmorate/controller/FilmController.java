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
        Film updatedFilm = updateExistingFilm(film);
        log.info("Фильм с id={} обновлён: {}", film.getId(), updatedFilm);
        return updatedFilm;
    }

    private Film updateExistingFilm(Film film) {
        validateFilmExists((long) film.getId());
        return saveUpdatedFilm(film);
    }

    private void validateFilmExists(Long filmId) {
        if (!films.containsKey(filmId)) {
            log.warn("Попытка обновления несуществующего фильма с id={}", filmId);
            throw new ValidationException("Фильм с id=" + filmId + " не найден");
        }
    }

    private Film saveUpdatedFilm(Film film) {
        films.put(film.getId(), film);
        return film;
    }

}
