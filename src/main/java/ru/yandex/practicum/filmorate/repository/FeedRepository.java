package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.FeedEvent;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FeedRepository {

    FeedEvent addFeedEvent(Long userId, String eventType, String operation, Long entityId);

    Collection<FeedEvent> getFeedEvents(Long userId);
}
