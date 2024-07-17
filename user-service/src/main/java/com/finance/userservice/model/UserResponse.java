package com.finance.userservice.model;

import lombok.Data;

@Data
public class UserResponse {

    private Long userId;
    private String name;
    private String email;
}
