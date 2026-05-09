package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")

public class UserController {

    private final Map<Long, User> users = new HashMap<>();
    private long generatedID = 0;

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        user.setId(++generatedID);

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        users.put((long) user.getId(), user);
        log.info("Добавлен новый пользователь: {}", user);
        return user;
    }

    @PutMapping
    public User update(@RequestBody User user) {
        User existingUser = users.get(user.getId());
        if (existingUser == null) {
            log.warn("Попытка обновления несуществующего пользователя с id={}", user.getId());
            throw new ValidationException("Пользователь с id=" + user.getId() + " не найден");
        }

        // Обновляем только не‑null поля
        if (user.getEmail() != null) {
            existingUser.setEmail(user.getEmail());
        }
        if (user.getLogin() != null) {
            existingUser.setLogin(user.getLogin());
        }
        if (user.getName() != null && !user.getName().isBlank()) {
            existingUser.setName(user.getName());
        } else if (user.getLogin() != null) {
            // Если имя пустое, используем логин
            existingUser.setName(user.getLogin());
        }
        if (user.getBirthday() != null) {
            existingUser.setBirthday(user.getBirthday());
        }

        log.info("Пользователь с id={} обновлён: {}", user.getId(), existingUser);
        return existingUser;
    }


    @GetMapping
    public List<User> getAllUsers() {
        log.info("Получен запрос на получение всех пользователей. Текущее количество: {}", users.size());
        return new ArrayList<>(users.values());
    }
}