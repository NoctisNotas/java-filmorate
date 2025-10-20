package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.Instant;

@Data
public class FeedEvent {
    private Long timestamp;
    private Long userId;
    private EventType eventType;
    private OperationType operation;
    private Long eventId;
    private Long entityId;
}
