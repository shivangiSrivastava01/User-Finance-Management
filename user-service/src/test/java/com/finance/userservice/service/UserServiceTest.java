package com.finance.userservice.service;

import com.finance.userservice.exception.UserCustomException;
import com.finance.userservice.model.User;
import com.finance.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;
    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId(1L);
        user.setName("Shivangi");
        user.setEmail("testUser@gmail.com");
    }

    @Test
    void testCreateUser_Success() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);

        User createdUser = userService.createUser(user);

        assertNotNull(createdUser);
        assertEquals(user.getEmail(), createdUser.getEmail());
    }

    @Test
    void testCreateUser_UserAlreadyExists() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        UserCustomException exception = assertThrows(UserCustomException.class, () -> userService.createUser(user));
        assertEquals("User already exists!!!!", exception.getMessage());
    }

    @Test
    void testGetUserFromId_Success() {

        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));

        Optional<User> retrievedUser = userService.getUserFromId(user.getUserId());

        assertTrue(retrievedUser.isPresent());
        assertEquals(user.getEmail(), retrievedUser.get().getEmail());
    }

    @Test
    void testGetUserFromId_UserNotFound() {

        when(userRepository.findById(user.getUserId())).thenReturn(Optional.empty());

        UserCustomException exception = assertThrows(UserCustomException.class, () -> userService.getUserFromId(user.getUserId()));
        assertEquals("User do not exists!!!!", exception.getMessage());
    }

    @Test
    void testCreateUser_RepositorySaveException() {

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Save failed"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.createUser(user));
        assertEquals("Save failed", exception.getMessage());
    }

    @Test
    void testGetUserFromId_RepositoryException() {

        when(userRepository.findById(user.getUserId())).thenThrow(new RuntimeException("Find failed"));
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.getUserFromId(user.getUserId()));
        assertEquals("Find failed", exception.getMessage());
    }
}
