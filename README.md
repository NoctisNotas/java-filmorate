# Filmorate - Social Network for film rating

Filmorate is a social network  where users can share information about films, rate them, and find like-minded people.

## Database Schema

![Database Schema](database_schema.svg)

## Database Structure Explanation

The database consists of 12 main tables:

### Core Tables:
- **users** - stores user information
- **films** - stores film information
- **mpa_ratings** - MPA rating catalog (G, PG, PG-13, R, NC-17)
- **genres** - film genres catalog
- **directors** - film directors catalog
- **reviews** - user reviews for films
- **feed** - user activity feed

### Relationship Tables:
- **film_genres** - many-to-many relationship between films and genres
- **film_likes** - tracks which users liked which films
- **friendship** - tracks friendships between users
- **film_directors** - many-to-many relationship between films and directors
- **review_likes** - tracks likes/dislikes for reviews

## Key SQL Query Examples

### 1. Get All Films with MPA Ratings and Genres
```sql
SELECT f.film_id, f.name, f.description, f.release_date, f.duration,
       m.name as mpa_rating,
       GROUP_CONCAT(g.name) as genres
FROM films f
LEFT JOIN mpa_ratings m ON f.mpa_id = m.mpa_id
LEFT JOIN film_genres fg ON f.film_id = fg.film_id
LEFT JOIN genres g ON fg.genre_id = g.genre_id
GROUP BY f.film_id;
```

### 2. Get Popular Films (Top N by Likes)
```sql
SELECT f.film_id, f.name, COUNT(fl.user_id) as likes_count
FROM films f
LEFT JOIN film_likes fl ON f.film_id = fl.film_id
GROUP BY f.film_id
ORDER BY likes_count DESC
LIMIT 10;
```

### 3. Get User's Friends
```sql
SELECT u.user_id, u.name, u.email
FROM friendship f
JOIN users u ON f.friend_id = u.user_id
WHERE f.user_id = 1;
```

### 4. Get Common Friends Between Two Users
```sql
SELECT u.user_id, u.name
FROM friendship f1
JOIN friendship f2 ON f1.friend_id = f2.friend_id
JOIN users u ON f1.friend_id = u.user_id
WHERE f1.user_id = 1 AND f2.user_id = 2;
```

### 5. Add Like to Film
```sql
INSERT INTO film_likes (film_id, user_id) 
VALUES (1, 1);
```

### 6. Add Friend
```sql
INSERT INTO friendship (user_id, friend_id, status) 
VALUES (1, 2, 'PENDING');
```

### 7. Get Films by Genre
```sql
SELECT f.film_id, f.name
FROM films f
JOIN film_genres fg ON f.film_id = fg.film_id
JOIN genres g ON fg.genre_id = g.genre_id
WHERE g.name = 'Комедия';
```

### 8. Update Film Information
```sql
UPDATE films 
SET name = 'New Film Name', description = 'New description', mpa_id = 3
WHERE film_id = 1;
```

### 9. Get User Feed Events
```sql
SELECT event_type, operation, entity_id, event_date
FROM feed 
WHERE user_id = 1 
ORDER BY event_date DESC;
```

### 10. Get Reviews with Usefulness Score
```sql
SELECT r.review_id, r.content, r.is_positive, r.useful,
       u.name as user_name, f.name as film_name
FROM reviews r
JOIN users u ON r.user_id = u.user_id
JOIN films f ON r.film_id = f.film_id
ORDER BY r.useful DESC;
```

### 11. Search Films by Director and Title
```sql
SELECT f.*, d.name as director_name
FROM films f
LEFT JOIN film_directors fd ON f.film_id = fd.film_id
LEFT JOIN directors d ON fd.director_id = d.director_id
WHERE LOWER(f.name) LIKE '%search_term%' 
   OR LOWER(d.name) LIKE '%search_term%';
```

### 12. Get Films by Director Sorted by Year
```sql
SELECT f.* 
FROM films f
JOIN film_directors fd ON f.film_id = fd.film_id
WHERE fd.director_id = 1
ORDER BY f.release_date ASC;
```

### 13. Add Review Like/Dislike
```sql
INSERT INTO review_likes (review_id, user_id, is_like) 
VALUES (1, 1, true);
```

### 14. Get Common Films Between Friends
```sql
SELECT f.* 
FROM films f
JOIN film_likes fl1 ON f.film_id = fl1.film_id
JOIN film_likes fl2 ON f.film_id = fl2.film_id
WHERE fl1.user_id = 1 AND fl2.user_id = 2;
```