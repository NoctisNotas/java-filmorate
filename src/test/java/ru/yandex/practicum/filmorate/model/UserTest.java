package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private Validator validator;
    private User user;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        user = new User();
        user.setEmail("test@example.com");
        user.setLogin("login");
        user.setName("Name");
        user.setBirthday(LocalDate.of(2000, 1, 1));
    }

    @Test
    void shouldPassValidationWithValidData() {
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(0, violations.size());
    }

    @Test
    void shouldFailWhenEmailIsNull() {
        user.setEmail(null);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertEquals("Адрес электронной почты не должен быть пустым", violations.iterator().next().getMessage());
    }

    @Test
    void shouldFailWhenEmailIsInvalid() {
        user.setEmail("invalid-email");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertEquals("Адрес электронной почты должен соответствовать формату", violations.iterator().next().getMessage());
    }

    @Test
    void shouldPassWhenEmailIsValid() {
        user.setEmail("valid.email+test@example.com");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(0, violations.size());
    }

    @Test
    void shouldFailWhenLoginIsNull() {
        user.setLogin(null);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertEquals("Логин не должен быть пустым", violations.iterator().next().getMessage());
    }

    @Test
    void shouldFailWhenLoginContainsSpaces() {
        user.setLogin("login with spaces");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertEquals("Логин не должен содержать пробелы", violations.iterator().next().getMessage());
    }

    @Test
    void shouldPassWhenLoginHasNoSpaces() {
        user.setLogin("valid_login123");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(0, violations.size());
    }

    @Test
    void shouldPassWhenNameIsNull() {
        user.setName(null);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(0, violations.size(), "Name может быть null");
    }

    @Test
    void shouldPassWhenNameIsBlank() {
        user.setName("");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(0, violations.size(), "Name может быть пустым");
    }

    @Test
    void shouldFailWhenBirthdayIsInFuture() {
        user.setBirthday(LocalDate.now().plusDays(1));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertEquals("Дата рождения не может быть в будущем", violations.iterator().next().getMessage());
    }

    @Test
    void shouldPassWhenBirthdayIsToday() {
        user.setBirthday(LocalDate.now());
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(0, violations.size());
    }

    @Test
    void shouldPassWhenBirthdayIsInPast() {
        user.setBirthday(LocalDate.now().minusDays(1));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(0, violations.size());
    }

    @Test
    void shouldPassWhenBirthdayIsNull() {
        user.setBirthday(null);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(0, violations.size(), "Birthday может быть null");
    }
}