package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.repository.DirectorRepository;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorRepository directorRepository;

    public Collection<Director> getAll() {
        return directorRepository.findAll();
    }

    public Director getDirector(Long id) {
        return directorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Режиссер с id = " + id + " не найден"));
    }

    public Director create(Director director) {
        if (director.getName() == null || director.getName().isBlank()) {
            throw new ValidationException("Имя режиссёра не может быть пустым.");
        }
        return directorRepository.save(director);
    }

    public Director update(Director director) {
        if (!directorRepository.existsById(director.getId())) {
            throw new NotFoundException("Режиссер не найден");
        }
        return directorRepository.update(director);
    }

    public void delete(Long id){
        if ( directorRepository.existsById(id)){
            directorRepository.delete(id);
        } else {
            throw new NotFoundException("При удалении режиссер не найден");
        }
    }

}
