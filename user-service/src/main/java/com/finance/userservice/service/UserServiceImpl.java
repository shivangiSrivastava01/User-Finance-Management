package com.finance.userservice.service;

import com.finance.userservice.exception.UserCustomException;
import com.finance.userservice.model.User;
import com.finance.userservice.model.UserRequest;
import com.finance.userservice.model.UserResponse;
import com.finance.userservice.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private static final  String NOT_FOUND_ERROR_MESSAGE = "User does not exist with Id: ";
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;

    }

    @Transactional
    @CacheEvict(value = "user-service", allEntries = true)
    public UserResponse createUser(UserRequest userRequest) {

       if(userRepository.findByEmail(userRequest.getEmail()).isPresent()) {
            throw new UserCustomException("User already exists!!!!");
        }

        User user = User
                .builder()
                .name(userRequest.getName())
                .email(userRequest.getEmail())
                .build();
       
        user = userRepository.save(user);

        UserResponse userResponse = new UserResponse();
        BeanUtils.copyProperties(user, userResponse);

        return userResponse;
    }

    @Cacheable(value = "user-service")
    public Optional<UserResponse> getUserFromId(Long userId) {

        if (userRepository.findById(userId).isEmpty()) {
            throw new UserCustomException("User do not exists!!!!");
        }

        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isPresent()) {
            UserResponse userResponse = new UserResponse();
            User user = userOptional.get(); // Unwrap the Optional
            BeanUtils.copyProperties(user, userResponse);

            return Optional.of(userResponse);
        }

        return Optional.empty();
    }

    /*
       Usage: Method to update the User Details.
    */
    @Transactional
    @CacheEvict(value = "user-service", allEntries = true)
    public void updateUserDetails(UserRequest userRequest) {

        log.info("Inside update user method::");
        Optional<User> checkIfUserExists = userRepository.findById(userRequest.getUserId());
        if (checkIfUserExists.isEmpty()) {
            throw new UserCustomException(NOT_FOUND_ERROR_MESSAGE + userRequest.getUserId());
        }

        User existingUser = checkIfUserExists.get();

        if(userRequest.getName()!=null) {
            existingUser.setName(userRequest.getName());
        }
        if (userRequest.getEmail()!=null) {
            existingUser.setEmail(userRequest.getEmail());
        }

        userRepository.save(existingUser);
    }

    /*
       Usage: Method to delete the user.
    */
    @Transactional
    @CacheEvict(value = "user-service", allEntries = true)
    public void deleteUser(Long userId) {

        log.info("Inside delete user method::");
        Optional<User> checkIfUserExists = userRepository.findById(userId);

        if (checkIfUserExists.isEmpty()) {
            throw new UserCustomException(NOT_FOUND_ERROR_MESSAGE + userId);
        }
        User existingUser = checkIfUserExists.get();
        userRepository.delete(existingUser);
    }
}
