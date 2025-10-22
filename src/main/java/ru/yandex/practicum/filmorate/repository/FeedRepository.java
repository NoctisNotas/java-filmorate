package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.FeedEvent;

import java.util.List;

public interface FeedRepository {

    FeedEvent addFeedEvent(Long userId, String eventType, String operation, Long entityId);

    List<FeedEvent> getFeedEvents(Long userId);
}
