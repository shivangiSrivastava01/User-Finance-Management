package com.finance.userservice.service;

import com.finance.userservice.model.UserRequest;
import com.finance.userservice.model.UserResponse;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface UserService {

    public UserResponse createUser(UserRequest userRequest);

    public Optional<UserResponse> getUserFromId(Long userId);

    public void updateUserDetails(UserRequest userRequest);

    public void deleteUser(Long userId);
}
