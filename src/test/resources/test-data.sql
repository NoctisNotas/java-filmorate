INSERT INTO mpa_ratings (mpa_id, name, description) VALUES
(1, 'G', 'Фильм без возрастных ограничений'),
(2, 'PG', 'Детям рекомендуется смотреть фильм с родителями'),
(3, 'PG-13', 'Детям до 13 лет просмотр не желателен'),
(4, 'R', 'Лицам до 17 лет просматривать фильм можно только в присутствии взрослого'),
(5, 'NC-17', 'Лицам до 18 лет просмотр запрещён');

INSERT INTO genres (genre_id, name) VALUES
(1, 'Комедия'),
(2, 'Драма'),
(3, 'Мультфильм'),
(4, 'Триллер'),
(5, 'Документальный'),
(6, 'Боевик');

INSERT INTO users (user_id, email, login, name, birthday) VALUES
(1, 'user1@example.com', 'user1', 'User One', '1990-01-01'),
(2, 'user2@example.com', 'user2', 'User Two', '1991-02-02'),
(3, 'user3@example.com', 'user3', 'User Three', '1992-03-03'),
(4, 'user4@example.com', 'user4', 'User Four', '1993-04-04');

INSERT INTO films (film_id, name, description, release_date, duration, mpa_id) VALUES
(1, 'Film One', 'Description for film one', '2000-01-01', 120, 1),
(2, 'Film Two', 'Description for film two', '2005-02-02', 150, 2),
(3, 'Film Three', 'Description for film three', '2010-03-03', 180, 3),
(4, 'Film Four', 'Description for film four', '2015-04-04', 90, 4);

INSERT INTO film_genres (film_id, genre_id) VALUES
(1, 1), (1, 2),
(2, 3),
(3, 4), (3, 5),
(4, 6);

INSERT INTO film_likes (film_id, user_id) VALUES
(1, 1), (1, 2),
(2, 1),
(3, 2), (3, 3), (3, 4);

INSERT INTO friendship (user_id, friend_id, status) VALUES
(1, 2, 'CONFIRMED'),
(1, 3, 'PENDING'),
(2, 3, 'CONFIRMED'),
(2, 4, 'CONFIRMED');