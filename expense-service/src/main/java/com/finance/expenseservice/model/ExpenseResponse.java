package com.finance.expenseservice.model;

import lombok.Data;

@Data
public class ExpenseResponse {

    private Long id;
    private String category;
    private double amount;
    private String description;
    private Long userId;
}
