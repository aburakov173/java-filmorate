package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ru.yandex.practicum.filmorate.FilmorateApplication.class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /users — 400, если email некорректный")
    void create_invalidEmail_returnsBadRequest() throws Exception {
        String body = objectMapper.writeValueAsString(Map.of(
                "email", "invalid",
                "login", "login",
                "name", "",
                "birthday", LocalDate.of(2000, 1, 1).toString()
        ));
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /users — 400, если дата рождения в будущем")
    void createUser_futureBirthday_returnsBadRequest() throws Exception {
        String body = objectMapper.writeValueAsString(Map.of(
                "email", "a@b.com",
                "login", "login",
                "name", "",
                "birthday", LocalDate.now().plusDays(1).toString()
        ));
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /users — 200 и в name подставляется login, если пусто")
    void createUser_validBoundary_setsNameFromLogin() throws Exception {
        String body = objectMapper.writeValueAsString(Map.of(
                "email", "a@b.com",
                "login", "login",
                "name", " ",
                "birthday", LocalDate.of(2000, 1, 1).toString()
        ));
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("login"));
    }

    @Test
    @DisplayName("GET /users — 200 и массив (может быть пустым)")
    void getUsers_returnsArray() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PUT /users — 200 при обновлении email и login")
    void updateUser_multipleFields_returnsOk() throws Exception {
        String createBody = objectMapper.writeValueAsString(Map.of(
                "email", "original@test.com",
                "login", "originalLogin",
                "name", "Original Name",
                "birthday", LocalDate.of(2000, 1, 1).toString()
        ));
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isOk());

        // обновляем email и login
        String updateBody = objectMapper.writeValueAsString(Map.of(
                "id", 1L,
                "email", "new@test.com",
                "login", "newLogin"
        ));

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("new@test.com"))
                .andExpect(jsonPath("$.login").value("newLogin"));

    }

}