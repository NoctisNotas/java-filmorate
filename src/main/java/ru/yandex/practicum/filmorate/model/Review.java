package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class Review {
    private Long reviewId;

    @NotBlank(message = "Текст отзыва не может быть пустым")
    private String content;

    @NotNull(message = "Необходимо поставить оценку отзыву")
    private Boolean isPositive;

    @NotNull(message = "ID пользователя не может быть пустым")
    private Long userId;

    @NotNull(message = "ID фильма не может быть пустым")
    private Long filmId;

    private Integer useful;
}
