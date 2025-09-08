package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> getAll() {
        log.info("Получение всех пользователей, найдено: {}", users.size());
        return users.values();
    }

    @Override
    public User getUser(Long id) {
        User user = users.get(id);
        if (user == null) {
            log.warn("Пользователь с id = {} не найден", id);
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
        return user;
    }

    @Override
    public User create(User user) {
        log.info("Создание пользователя: {}", user.getId());
        if (user.getName() == null || user.getName().isBlank()) {
            log.debug("Имя пользователя null, устанавливаем логин: {}", user.getLogin());
            user.setName(user.getLogin());
        }

        user.setId(getNextId());
        users.put(user.getId(), user);

        log.info("Пользователь создан успешно, ID: {}", user.getId());
        return user;
    }

    @Override
    public User update(User user) {
        log.info("Обновление пользователя: {}", user.getId());

        if (!users.containsKey(user.getId())) {
            log.warn("Ошибка обновления: пользователь с id = {} не найден", user.getId());
            throw new NotFoundException("Пользователь с id = " + user.getId() + " не найден");
        }

        users.put(user.getId(), user);

        log.info("Пользователь обновлен успешно, ID: {}", user.getId());
        return user;
    }

    private Long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
