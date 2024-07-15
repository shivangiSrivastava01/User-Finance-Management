package com.finance.userservice.controller;

import com.finance.userservice.exception.UserCustomException;
import com.finance.userservice.model.User;
import com.finance.userservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        userController = Mockito.spy(new UserController(this.userService));
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateUser_Failure() {
        User user = new User();
        lenient().when(userService.createUser(user)).thenThrow(new UserCustomException("Error creating user"));

        ResponseEntity<String> response = userController.createUser(user);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void testGetUserWithId_Exception() {
        Long userId = 1L;
        lenient().when(userService.getUserFromId(userId)).thenThrow(new RuntimeException("Internal error"));

        ResponseEntity<User> response = userController.getUserWithId(userId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void updateBudget_success() {
        User user = new User();

        ResponseEntity<String> response = userController.updateUser(user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User details updated successfully", response.getBody());
    }

    @Test
    void deleteBudget_success() {
        ResponseEntity<String> response = userController.deleteUser(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User deleted successfully", response.getBody());
    }

}
