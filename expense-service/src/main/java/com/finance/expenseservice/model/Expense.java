package com.finance.expenseservice.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Entity
@Table(name="expenses")
@Data
public class Expense {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    @PositiveOrZero(message="Amount should be zero or positive")
    private double amount;

    //here description is marked as not null, because we need to send the expense description in notification mail
    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Long userId;
}
