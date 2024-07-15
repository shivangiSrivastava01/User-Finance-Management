package com.finance.expenseservice.dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Data;


@Entity
@Data
public class UserDTO {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long userId;

    private String name;

    @Email
    private String email;

}
