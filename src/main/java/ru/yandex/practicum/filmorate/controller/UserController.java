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
        User savedUser = processAndSaveUser(user);
        log.info("Добавлен новый пользователь: {}", savedUser);
        return savedUser;
    }

    private User processAndSaveUser(User user) {
        assignId(user);
        processUserName(user);
        storeUser(user);
        return user;
    }

    private void assignId(User user) {
        user.setId(++generatedID);
    }

    private void processUserName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    private void storeUser(User user) {
        users.put((long) user.getId(), user);
    }


    @PutMapping
    public User update(@RequestBody User user) {
        User updatedUser = updateExistingUser(user);
        log.info("Пользователь с id={} обновлён: {}", user.getId(), updatedUser);
        return updatedUser;
    }

    private User updateExistingUser(User user) {
        validateUserExists(user.getId());
        applyUserUpdates(user);
        return users.get(user.getId());
    }

    private void validateUserExists(Long userId) {
        if (users.get(userId) == null) {
            log.warn("Попытка обновления несуществующего пользователя с id={}", userId);
            throw new ValidationException("Пользователь с id=" + userId + " не найден");
        }
    }

    private void applyUserUpdates(User user) {
        User existingUser = users.get(user.getId());

        updateEmailIfProvided(user, existingUser);
        updateLoginIfProvided(user, existingUser);
        updateNameIfProvided(user, existingUser);
        updateBirthdayIfProvided(user, existingUser);
    }

    private void updateEmailIfProvided(User user, User existingUser) {
        if (user.getEmail() != null) {
            existingUser.setEmail(user.getEmail());
        }
    }

    private void updateLoginIfProvided(User user, User existingUser) {
        if (user.getLogin() != null) {
            existingUser.setLogin(user.getLogin());
        }
    }

    private void updateNameIfProvided(User user, User existingUser) {
        if (user.getName() != null && !user.getName().isBlank()) {
            existingUser.setName(user.getName());
        } else if (user.getLogin() != null) {
            // Если имя пустое, используем логин
            existingUser.setName(user.getLogin());
        }
    }

    private void updateBirthdayIfProvided(User user, User existingUser) {
        if (user.getBirthday() != null) {
            existingUser.setBirthday(user.getBirthday());
        }
    }



    @GetMapping
    public List<User> getAllUsers() {
        log.info("Получен запрос на получение всех пользователей. Текущее количество: {}", users.size());
        return new ArrayList<>(users.values());
    }
}