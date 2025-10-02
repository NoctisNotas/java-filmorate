package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({JdbcUserRepository.class, UserMapper.class})
class JdbcUserRepositoryTest {

    private final JdbcUserRepository userRepository;

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
    void testSaveUser() {
        User newUser = new User();
        newUser.setEmail("newuser@example.com");
        newUser.setLogin("newuser");
        newUser.setName("New User");
        newUser.setBirthday(LocalDate.of(1995, 5, 5));

        User savedUser = userRepository.save(newUser);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo("newuser@example.com");

        Collection<User> allUsers = userRepository.findAll();
        assertThat(allUsers).hasSize(5);
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
}