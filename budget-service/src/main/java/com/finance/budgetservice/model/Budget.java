package com.finance.budgetservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;


@Entity
@Table(name = "budgets", uniqueConstraints={@UniqueConstraint(columnNames = {"user_id", "category"})})
@Data
public class Budget {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    @PositiveOrZero(message="Amount should be zero or positive")
    private double amount;

    @Column(nullable = false)
    private Long userId;

}
