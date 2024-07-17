package com.finance.budgetservice.model;

import lombok.Data;

@Data
public class BudgetResponse {

    private Long id;
    private String category;
    private double amount;
    private Long userId;
}
