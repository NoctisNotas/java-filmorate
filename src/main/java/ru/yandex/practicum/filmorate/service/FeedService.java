package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.filmorate.model.FeedEvent;
import ru.yandex.practicum.filmorate.repository.FeedRepository;

import java.util.Collection;

@Service
@Slf4j
@RequiredArgsConstructor
public class FeedService {
    private final FeedRepository feedRepository;

    public FeedEvent addFeedEvent(Long userId, String eventType, String operation, Long entityId) {
        return feedRepository.addFeedEvent(userId, eventType, operation, entityId);
    }

    public Collection<FeedEvent> getFeedEvents(Long userId) {
        return feedRepository.getFeedEvents(userId);
    }
}
