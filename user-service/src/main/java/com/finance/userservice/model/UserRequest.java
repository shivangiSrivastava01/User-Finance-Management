package com.finance.userservice.model;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UserRequest {

    private Long userId;
    private String name;
    @Email
    private String email;
}
