package com.finance.expenseservice.service;

import com.finance.expenseservice.model.ExpenseRequest;
import com.finance.expenseservice.model.ExpenseResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface ExpenseService {


    public Optional<List<ExpenseResponse>> getUserSpecificExpense(Long userId);

    public Optional<ExpenseResponse> getExpenseWithId(Long expenseId);

    public List<ExpenseResponse> getUserCategoryExpense(Long userId, String category);

    public ExpenseResponse logExpense(ExpenseRequest expenseRequest);

    public ExpenseResponse updateExpense(ExpenseRequest expense);

    public void deleteExpense(Long id);

    public double getCategoryExpenseTotalAmount(Long userId, String category);
}
