package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.repository.MpaRatingRepository;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class MpaRatingService {
    private final MpaRatingRepository mpaRatingRepository;

    public Collection<MpaRating> getAll() {
        return mpaRatingRepository.findAll();
    }

    public MpaRating getMpaRating(Long id) {
        return mpaRatingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("MPA рейтинг с id = " + id + " не найден"));
    }
}