package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final FeedService feedService;
    private final FilmRepository filmRepository;

    public Collection<User> getAll() {
        return userRepository.findAll();
    }

    public User create(User user) {
        return userRepository.save(user);
    }

    public User update(User user) {
        if (!userRepository.existsById(user.getId())) {
            throw new NotFoundException("Пользователь с id = " + user.getId() + " не найден");
        }

        return userRepository.update(user);
    }

    public User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + id + " не найден"));
    }

    public void addFriend(Long id, Long friendId) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
        if (!userRepository.existsById(friendId)) {
            throw new NotFoundException("Пользователь с id " + friendId + " не найден");
        }
        userRepository.addFriend(id, friendId);
        feedService.addFeedEvent(id, "FRIEND", "ADD", friendId);
    }

    public void deleteFriend(Long id, Long friendId) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
        if (!userRepository.existsById(friendId)) {
            throw new NotFoundException("Пользователь с id = " + friendId + " не найден");
        }

        userRepository.removeFriend(id, friendId);
        feedService.addFeedEvent(id, "FRIEND", "REMOVE", friendId);
    }

    public Collection<User> getFriends(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }

        return userRepository.findFriends(id);
    }

    public Collection<User> findCommonFriends(Long id, Long otherUserId) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
        if (!userRepository.existsById(otherUserId)) {
            throw new NotFoundException("Пользователь с id = " + otherUserId + " не найден");
        }

        return userRepository.findCommonFriends(id, otherUserId);
    }

    public List<Film> getRecommendationsAboutFilms(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
        if (!userRepository.userHasLike(id)) {
            return Collections.emptyList();
        }
        List<Long> users = userRepository.getUsersWithSameLikes(id);
        List<Film> recommendFilms = new ArrayList<>();
        if (users == null || users.isEmpty()) {
            return Collections.emptyList();
        } else {
            recommendFilms = filmRepository.getFilmsFromUsersThatLiked(users);
            List<Long> usersFilms = filmRepository.getFilmsFromUser(id);
            recommendFilms = recommendFilms.stream()
                    .filter(film -> !usersFilms.contains(film.getId()))
                    .collect(Collectors.toList());
        }
        return recommendFilms;
    }

}
