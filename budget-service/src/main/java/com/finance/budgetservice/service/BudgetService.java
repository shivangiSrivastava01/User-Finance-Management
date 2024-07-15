package com.finance.budgetservice.service;

import com.finance.budgetservice.exception.BudgetCustomException;
import com.finance.budgetservice.model.Budget;
import com.finance.budgetservice.repository.BudgetRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private static final  String NOT_FOUND_ERROR_MESSAGE = "Budget does not exist with Id: ";

    @Autowired
    public BudgetService(BudgetRepository budgetRepository) {
        this.budgetRepository = budgetRepository;
    }

    /*
       Usage: Method to fetch all the budget for a particular user.
    */
    @Cacheable(value = "budget-service")
    public Optional<List<Budget>> getUserSpecificBudget(Long userId) {
        log.info("Fetch budget specific to userID::");
        return budgetRepository.findByUserId(userId);
    }

    /*
       Usage: Method to fetch all the budget for a particular budgetId.
    */
    @Cacheable(value = "budget-service")
    public Optional<Budget> getBudgetFromId(Long budgetId) {
        log.info("Fetch Budget specific to budgetID::");
        return budgetRepository.findById(budgetId);
    }

    /*
       Usage: Method to save a new budget for a user.
    */
    @Transactional
    @CacheEvict(value = "budget-service", allEntries = true)
    public Budget createBudget(Budget budget) {
        if (budgetRepository.findBudgetByUserCategory(budget.getUserId(),budget.getCategory()).isPresent()) {
            throw new BudgetCustomException("Budget already exists!!!!");
        }
        log.info("Creating Budget::::");
        return budgetRepository.save(budget);

    }

    /*
       Usage: Method to update the budget.
    */
    @Transactional
    @CacheEvict(value = "budget-service", allEntries = true)
    public void updateBudget(Budget budget) {

        log.info("Inside update budget method::");
        Optional<Budget> checkIfBudgetExists = budgetRepository.findById(budget.getId());
        if (checkIfBudgetExists.isEmpty()) {
            throw new BudgetCustomException(NOT_FOUND_ERROR_MESSAGE + budget.getId());
        }

        Budget existingBudget = checkIfBudgetExists.get();

        if(budget.getCategory()!=null) {
            existingBudget.setCategory(budget.getCategory());
        }
        if (budget.getAmount()>0) {
            existingBudget.setAmount(budget.getAmount());
        }

        budgetRepository.save(existingBudget);
    }

    /*
       Usage: Method to delete the budget.
    */
    @Transactional
    @CacheEvict(value = "budget-service", allEntries = true)
    public void deleteBudget(Long userId) {

        log.info("Inside delete budget method::");
        Optional<Budget> checkIfBudgetExists = budgetRepository.findById(userId);

        if (checkIfBudgetExists.isEmpty()) {
            throw new BudgetCustomException(NOT_FOUND_ERROR_MESSAGE + userId);
        }
        Budget existingBudget = checkIfBudgetExists.get();
        budgetRepository.delete(existingBudget);
    }

    /*
       Usage: Method to fetch the budget with respect to user and category.
    */
    @Cacheable(value = "budget-service")
    public Budget getUserCategoryBudget(Long userId, String category) {

        Optional<Budget> checkIfBudgetExists = budgetRepository.findBudgetByUserCategory(userId, category);

        if (checkIfBudgetExists.isEmpty()) {
            throw new BudgetCustomException(NOT_FOUND_ERROR_MESSAGE + userId);
        }
        log.info("Get Budget by userID and Category:: {}", checkIfBudgetExists.get());
        return checkIfBudgetExists.get();
    }
}
