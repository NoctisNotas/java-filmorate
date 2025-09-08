package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> getAll() {
        return userStorage.getAll();
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public User getUser(Long id) {
        return userStorage.getUser(id);
    }

    public void addFriend(Long id, Long friendId) {
        log.debug("Запрос на добавление в друзья: пользователь {} хочет добавить пользователя {}", id, friendId);
        User user = userStorage.getUser(id);
        if (user == null) {
            log.warn("Пользователь с id {} не найден", id);
            throw new NotFoundException("Пользователь с id  " + id + " не найден");
        }

        User friend = userStorage.getUser(friendId);
        if (friend == null) {
            log.warn("Пользователь с id {} не найден", friendId);
            throw new NotFoundException("Пользователь с id  " + friendId + " не найден");
        }

        user.getFriends().add(friendId);
        friend.getFriends().add(id);

        userStorage.update(user);
        userStorage.update(friend);
        log.info("Пользователи {} и {} успешно добавлены в друзья", id, friendId);
    }

    public void deleteFriend(Long id, Long friendId) {
        log.debug("Запрос на удаление из друзей: пользователь {} хочет удалить пользователя {}", id, friendId);
        User user = userStorage.getUser(id);
        User friend = userStorage.getUser(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(id);

        userStorage.update(user);
        userStorage.update(friend);
        log.debug("Пользователь {} удалил из друзей пользователя {}", id, friendId);
    }

    public Collection<User> getFriends(Long id) {
        log.debug("Запрос на получение списка друзей пользователя с ID: {}", id);
        User user = userStorage.getUser(id);
        log.info("Получен список друзей для пользователя с ID: {}", id);
        return user.getFriends().stream()
                .map(userStorage::getUser)
                .collect(Collectors.toList());
    }

    public Collection<User> findCommonFriends(Long id, Long otherUserId) {
        log.debug("Запрос на поиск общих друзей между пользователями {} и {}", id, otherUserId);
        User user = userStorage.getUser(id);
        User otherUser = userStorage.getUser(otherUserId);

        Collection<User> commonFriends = user.getFriends().stream()
                .filter(otherUser.getFriends()::contains)
                .map(userStorage::getUser)
                .collect(Collectors.toList());
        log.info("Найдено {} общих друзей между пользователями {} и {}", commonFriends.size(), id, otherUserId);

        return commonFriends;
    }
}
