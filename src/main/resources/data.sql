INSERT INTO mpa_ratings (name, description)
SELECT 'G', 'Фильм без возрастных ограничений' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM mpa_ratings WHERE name = 'G');

INSERT INTO mpa_ratings (name, description)
SELECT 'PG', 'Детям рекомендуется смотреть фильм с родителями' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM mpa_ratings WHERE name = 'PG');

INSERT INTO mpa_ratings (name, description)
SELECT 'PG-13', 'Детям до 13 лет просмотр не желателен' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM mpa_ratings WHERE name = 'PG-13');

INSERT INTO mpa_ratings (name, description)
SELECT 'R', 'Лицам до 17 лет просматривать фильм можно только в присутствии взрослого' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM mpa_ratings WHERE name = 'R');

INSERT INTO mpa_ratings (name, description)
SELECT 'NC-17', 'Лицам до 18 лет просмотр запрещён' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM mpa_ratings WHERE name = 'NC-17');


INSERT INTO genres (name)
SELECT 'Комедия' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM genres WHERE name = 'Комедия');

INSERT INTO genres (name)
SELECT 'Драма' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM genres WHERE name = 'Драма');

INSERT INTO genres (name)
SELECT 'Мультфильм' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM genres WHERE name = 'Мультфильм');

INSERT INTO genres (name)
SELECT 'Триллер' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM genres WHERE name = 'Триллер');

INSERT INTO genres (name)
SELECT 'Документальный' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM genres WHERE name = 'Документальный');

INSERT INTO genres (name)
SELECT 'Боевик' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM genres WHERE name = 'Боевик');