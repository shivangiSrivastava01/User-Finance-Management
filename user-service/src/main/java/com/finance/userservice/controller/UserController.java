package com.finance.userservice.controller;

import com.finance.userservice.exception.UserCustomException;
import com.finance.userservice.model.User;
import com.finance.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/financeManagement")
@Tag(name = "User Service", description = "Operations pertaining to user in Finance Management System")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }
    /*
    Usage: This API saves a user.
    */
    @Operation(summary = "Add a user")
    @PostMapping("/users")
    public ResponseEntity<String> createUser(@Valid @RequestBody User user) {
        try {
            log.info("Inside createUser method::");
            User userData = userService.createUser(user);
            if(userData==null){
                throw new UserCustomException("User do not exists in DB");
            }
            return new ResponseEntity<>("User created successfully", HttpStatus.CREATED);

        } catch (UserCustomException e) {
            log.error("Exception occurred while creating the user data: {}", e.getMessage());
            return new ResponseEntity<>("Error creating user: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /*
    Usage: This API fetches user with respect to user id.
    */
    @Operation(summary = "View user with respect to user id")
    @GetMapping("/userById")
    public ResponseEntity<User> getUserWithId(@RequestParam Long userId) {
        try {
            Optional<User> user = userService.getUserFromId(userId);
            if (user.isPresent()) {
                log.info("Get user with userID :: {}", user.get());
                return new ResponseEntity<>(user.get(), HttpStatus.OK);
            } else {
                throw new UserCustomException("user not found for user Id: " + userId);
            }
        } catch (UserCustomException e) {
            log.error("User data not found with respect to userID: {}",userId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Exception occurred while retrieving the user data with respect to userID: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /*
       Usage: This API updates User Details.
    */
    @Operation(summary = "Update User Details")
    @PutMapping("/UserDetailsUpdate")
    public ResponseEntity<String> updateUser(@Valid @RequestBody User user) {
        try {
            log.info("Inside update method::");
            userService.updateUserDetails(user);
            return new ResponseEntity<>("User details updated successfully", HttpStatus.OK);

        } catch (UserCustomException e) {
            log.info("Exception occurred while updating the user data: {}", e.getMessage());
            return new ResponseEntity<>("Error updating user: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /*
       Usage: This API deletes the User.
    */
    @Operation(summary = "Delete a User")
    @DeleteMapping("/userDeletion/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        try {
            log.info("Inside deleteUser method::");
            userService.deleteUser(id);
            return new ResponseEntity<>("User deleted successfully", HttpStatus.OK);
        } catch (UserCustomException e) {
            log.info("Exception occurred while deleting the user data: {}", e.getMessage());
            return new ResponseEntity<>("Error deleting user: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
