package com.finance.userservice.service;

import com.finance.userservice.exception.UserCustomException;
import com.finance.userservice.model.User;
import com.finance.userservice.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Slf4j
@Service
public class UserService {

    private static final  String NOT_FOUND_ERROR_MESSAGE = "User does not exist with Id: ";
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;

    }

    @Transactional
    @CacheEvict(value = "user-service", allEntries = true)
    public User createUser(User user) {

       if(userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new UserCustomException("User already exists!!!!");
        }
        return userRepository.save(user);
    }

    @Cacheable(value = "user-service")
    public Optional<User> getUserFromId(Long userId) {

        if (userRepository.findById(userId).isEmpty()) {
            throw new UserCustomException("User do not exists!!!!");
        }
        return userRepository.findById(userId);
    }

    /*
       Usage: Method to update the User Details.
    */
    @Transactional
    @CacheEvict(value = "user-service", allEntries = true)
    public void updateUserDetails(User user) {

        log.info("Inside update user method::");
        Optional<User> checkIfUserExists = userRepository.findById(user.getUserId());
        if (checkIfUserExists.isEmpty()) {
            throw new UserCustomException(NOT_FOUND_ERROR_MESSAGE + user.getUserId());
        }

        User existingUser = checkIfUserExists.get();

        if(user.getName()!=null) {
            existingUser.setName(user.getName());
        }
        if (user.getEmail()!=null) {
            existingUser.setEmail(user.getEmail());
        }

        userRepository.save(existingUser);
    }

    /*
       Usage: Method to delete the budget.
    */
    @Transactional
    @CacheEvict(value = "user-service", allEntries = true)
    public void deleteUser(Long userId) {

        log.info("Inside delete user method::");
        Optional<User> checkIfBudgetExists = userRepository.findById(userId);

        if (checkIfBudgetExists.isEmpty()) {
            throw new UserCustomException(NOT_FOUND_ERROR_MESSAGE + userId);
        }
        User existingBudget = checkIfBudgetExists.get();
        userRepository.delete(existingBudget);
    }
}
