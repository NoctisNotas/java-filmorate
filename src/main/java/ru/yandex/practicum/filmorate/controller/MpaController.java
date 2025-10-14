package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.service.MpaRatingService;

import java.util.Collection;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {
    private final MpaRatingService mpaRatingService;

    @GetMapping
    public Collection<MpaRating> getAll() {
        return mpaRatingService.getAll();
    }

    @GetMapping("/{id}")
    public MpaRating getMpa(@PathVariable Long id) {
        return mpaRatingService.getMpaRating(id);
    }
}