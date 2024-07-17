package com.finance.budgetservice.service;

import com.finance.budgetservice.model.BudgetRequest;
import com.finance.budgetservice.model.BudgetResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface BudgetService {

    public Optional<List<BudgetResponse>> getUserSpecificBudget(Long userId);

    public Optional<BudgetResponse> getBudgetFromId(Long budgetId);

    public BudgetResponse createBudget(BudgetRequest budgetRequest);

    public void updateBudget(BudgetRequest budgetRequest);

    public void deleteBudget(Long userId);

    public BudgetResponse getUserCategoryBudget(Long userId, String category);
}
