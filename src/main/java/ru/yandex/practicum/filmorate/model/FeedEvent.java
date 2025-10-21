package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FeedEvent {
    private Long timestamp;

    @NotNull(message = "ID пользователя не может быть пустым")
    private Long userId;

    private EventType eventType;
    private OperationType operation;
    private Long eventId;

    @NotNull(message = "ID сущности не может быть пустым")
    private Long entityId;
}
