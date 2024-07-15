package com.finance.expenseservice.dto;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class BudgetDTO {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String category;

    @Column(nullable=false)
    private double amount;

    @Column(nullable=false)
    private Long userId;
}
