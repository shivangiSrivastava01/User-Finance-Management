package com.finance.expenseservice.service;

import com.finance.expenseservice.exception.ExpenseCustomException;
import com.finance.expenseservice.model.Expense;
import com.finance.expenseservice.repository.ExpenseRepository;
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
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    @Autowired
    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    /*
        Usage: Method to fetch the user with respect to userId.
    */
    @Cacheable(value = "expense-service")
    public Optional<List<Expense>> getUserSpecificExpense(Long userId) {
        log.info("Fetch expense specific to userID::");
        return expenseRepository.findByUserId(userId);
    }

    @Cacheable(value = "expense-service")
    public Optional<Expense> getExpenseWithId(Long expenseId) {
        log.info("Fetch expense specific to expenseID::");
        return expenseRepository.findById(expenseId);
    }

    /*
        Usage: Method to fetch the user with respect to userId and category.
    */
    @Cacheable(value = "expense-service")
    public List<Expense> getUserCategoryExpense(Long userId, String category) {

        Optional<List<Expense>> checkIfExpenseExists = expenseRepository.findExpenseByUserCategory(userId, category.toLowerCase());

        if (checkIfExpenseExists.isPresent() && !checkIfExpenseExists.get().isEmpty()) {
            log.info("Get expense by userID and Category:: {}", checkIfExpenseExists.get());
            return checkIfExpenseExists.get();
        } else {
            throw new ExpenseCustomException("Expense not found for User Id: " + userId);
        }
    }

    /*
        Usage: Method to log an expense to DB.
    */
    @Transactional
    @CacheEvict(value = "expense-service", allEntries = true)
    public Expense logExpense(Expense expense) {
        return expenseRepository.save(expense);
    }


    /*
        Usage: Method to update the expense.
    */
    @Transactional
    @CacheEvict(value = "expense-service", allEntries = true)
    public Expense updateExpense(Expense expense) {

        log.info("Inside update expense Method::");
        Optional<Expense> checkIfExpenseExists = expenseRepository.findById(expense.getId());

        if (checkIfExpenseExists.isEmpty()) {
            throw new ExpenseCustomException("Expense does not exist with Id: " + expense.getId());
        }
        Expense existingExpense = checkIfExpenseExists.get();

        if(expense.getCategory()!=null) {
            existingExpense.setCategory(expense.getCategory());
        }
        if(expense.getDescription()!=null) {
            existingExpense.setDescription(expense.getDescription());
        }
        if (expense.getAmount()>0) {
            existingExpense.setAmount(expense.getAmount());
        }
        expenseRepository.save(existingExpense);
        return existingExpense;
    }

    /*
        Usage: Method to delete the expense.
    */
    @Transactional
    @CacheEvict(value = "expense-service", allEntries = true)
    public void deleteExpense(Long id) {
        log.info("Inside delete budget Method::");
        if (!expenseRepository.existsById(id)) {
            throw new ExpenseCustomException("expense does not exist with Id: " + id);
        }
        expenseRepository.deleteById(id);
    }

    public double getCategoryExpenseTotalAmount(Long userId, String category) {

        Optional<Double> checkIfExpenseExists = expenseRepository.findSumExpenseByUserCategory(userId, category.toLowerCase());
        if(checkIfExpenseExists.isPresent()){
            log.info("Get sum of total expense for a Category:: {}", checkIfExpenseExists.get());
            return checkIfExpenseExists.get();
        }else{
            throw new ExpenseCustomException("Expense does not exist!!!");
        }

    }
}
