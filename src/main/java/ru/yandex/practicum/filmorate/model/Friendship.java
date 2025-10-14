package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Friendship {
    private Long friendId;
    private FriendshipStatus status;

    public Friendship() {
    }

    public Friendship(Long friendId, FriendshipStatus status) {
        this.friendId = friendId;
        this.status = status;
    }
}