package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({JdbcUserRepository.class, UserMapper.class})
class JdbcUserRepositoryTest {

    private final JdbcUserRepository userRepository;
    private final JdbcTemplate jdbcTemplate;

    @Test
    void testFindUserById() {
        Optional<User> userOptional = userRepository.findById(1L);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user -> {
                    assertThat(user.getId()).isEqualTo(1L);
                    assertThat(user.getEmail()).isEqualTo("user1@example.com");
                    assertThat(user.getLogin()).isEqualTo("user1");
                    assertThat(user.getName()).isEqualTo("User One");
                });
    }

    @Test
    void testFindAllUsers() {
        Collection<User> users = userRepository.findAll();

        assertThat(users).hasSize(4);
    }

    @Test
    void testUpdateUser() {
        Optional<User> existingUser = userRepository.findById(1L);
        assertThat(existingUser).isPresent();

        User userToUpdate = existingUser.get();
        userToUpdate.setName("Updated Name");
        userToUpdate.setEmail("updated@example.com");

        User updatedUser = userRepository.update(userToUpdate);

        assertThat(updatedUser.getName()).isEqualTo("Updated Name");
        assertThat(updatedUser.getEmail()).isEqualTo("updated@example.com");

        Optional<User> retrievedUser = userRepository.findById(1L);
        assertThat(retrievedUser)
                .isPresent()
                .hasValueSatisfying(user -> {
                    assertThat(user.getName()).isEqualTo("Updated Name");
                    assertThat(user.getEmail()).isEqualTo("updated@example.com");
                });
    }

    @Test
    void testExistsById() {
        assertThat(userRepository.existsById(1L)).isTrue();
        assertThat(userRepository.existsById(999L)).isFalse();
    }

    @Test
    void testFindFriends() {
        Collection<User> friends = userRepository.findFriends(1L);

        assertThat(friends).hasSize(2);
        assertThat(friends)
                .extracting(User::getId)
                .containsExactlyInAnyOrder(2L, 3L);
    }

    @Test
    void testFindCommonFriends() {
        Collection<User> commonFriends = userRepository.findCommonFriends(1L, 2L);

        assertThat(commonFriends).hasSize(1);
        assertThat(commonFriends.iterator().next().getId()).isEqualTo(3L);
    }

    @Test
    void testSaveUser() {
        jdbcTemplate.update("DELETE FROM friendship");
        jdbcTemplate.update("DELETE FROM users");

        jdbcTemplate.execute("ALTER TABLE users ALTER COLUMN user_id RESTART WITH 1");

        User newUser = new User();
        newUser.setEmail("newuser@example.com");
        newUser.setLogin("newuser");
        newUser.setName("New User");
        newUser.setBirthday(LocalDate.of(1995, 5, 5));

        User savedUser = userRepository.save(newUser);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isEqualTo(1L); // Теперь будет 1
        assertThat(savedUser.getEmail()).isEqualTo("newuser@example.com");
        assertThat(savedUser.getLogin()).isEqualTo("newuser");
        assertThat(savedUser.getName()).isEqualTo("New User");
        assertThat(savedUser.getBirthday()).isEqualTo(LocalDate.of(1995, 5, 5));

        Collection<User> allUsers = userRepository.findAll();
        assertThat(allUsers).hasSize(1); // Теперь только 1 пользователь
    }

    @Test
    void testAddAndRemoveFriend() {
        userRepository.addFriend(1L, 4L);

        Collection<User> friendsAfterAdd = userRepository.findFriends(1L);
        assertThat(friendsAfterAdd)
                .extracting(User::getId)
                .contains(4L);

        userRepository.removeFriend(1L, 4L);

        Collection<User> friendsAfterRemove = userRepository.findFriends(1L);
        assertThat(friendsAfterRemove)
                .extracting(User::getId)
                .doesNotContain(4L);
    }

    @Test
    void testDeleteUser() {
        userRepository.deleteById(1L);

        Optional<User> userAfterDelete = userRepository.findById(1L);
        assertThat(userAfterDelete).isEmpty();

        boolean exists = userRepository.existsById(1L);
        assertThat(exists).isFalse();
    }
}
    void testUserHasLike() {
        assertThat(userRepository.userHasLike(1L)).isTrue();
    }

    @Test
    void testGetUsersWithSameLikes() {
        List<Long> users = userRepository.getUsersWithSameLikes(3L);

        assertThat(users.size()).isEqualTo(2);
        assertThat(users.contains(2L)).isTrue();
        assertThat(users.contains(4L)).isTrue();
        assertThat(users.contains(3L)).isFalse();
    }

}
