package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.JdbcFeedRepository;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.Collection;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final FeedService feedService;

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
}