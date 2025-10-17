package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.User;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Collection<User> findAll();

    Optional<User> findById(Long id);

    User save(User user);

    User update(User user);

    void addFriend(Long userId, Long friendId);

    void removeFriend(Long userId, Long friendId);

    Collection<User> findFriends(Long userId);

    Collection<User> findCommonFriends(Long userId, Long otherUserId);

    boolean existsById(Long id);

    List<Long> getUsersWithSameLikes(Long userId);

    boolean userHasLike(Long id);
}