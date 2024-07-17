package com.finance.expenseservice.service;

import com.finance.expenseservice.exception.ExpenseCustomException;
import com.finance.expenseservice.model.Expense;
import com.finance.expenseservice.model.ExpenseRequest;
import com.finance.expenseservice.model.ExpenseResponse;
import com.finance.expenseservice.repository.ExpenseRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ExpenseServiceImpl implements ExpenseService{

    private final ExpenseRepository expenseRepository;

    @Autowired
    public ExpenseServiceImpl(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    /*
        Usage: Method to fetch the user with respect to userId.
    */
    @Cacheable(value = "expense-service")
    public Optional<List<ExpenseResponse>> getUserSpecificExpense(Long userId) {
        log.info("Fetch expense specific to userID::");
        Optional<List<Expense>> expense = expenseRepository.findByUserId(userId);

        if(expense.isPresent() && !expense.get().isEmpty()) {
            List<ExpenseResponse> expenseResponse = new ArrayList<>();

            for(Expense b : expense.get()) {
                ExpenseResponse br = new ExpenseResponse();
                BeanUtils.copyProperties(b, br);
                expenseResponse.add(br);
            }

            return Optional.of(expenseResponse);
        }
        return Optional.empty();
    }


    public Optional<ExpenseResponse> getExpenseWithId(Long expenseId) {
        log.info("Fetch expense specific to expenseID::");
        Optional<Expense> expenseOptional = expenseRepository.findById(expenseId);

        if (expenseOptional.isPresent()) {
            ExpenseResponse expenseResponse = new ExpenseResponse();
            Expense expense = expenseOptional.get(); // Unwrap the Optional
            BeanUtils.copyProperties(expense, expenseResponse);

            return Optional.of(expenseResponse);
        }

        return Optional.empty();

    }

    /*
        Usage: Method to fetch the user with respect to userId and category.
    */
    @Cacheable(value = "expense-service")
    public List<ExpenseResponse> getUserCategoryExpense(Long userId, String category) {

        Optional<List<Expense>> checkIfExpenseExists = expenseRepository.findExpenseByUserCategory(userId, category.toLowerCase());

        if (checkIfExpenseExists.isPresent()) {
            log.info("Get expense by userID and Category:: {}", checkIfExpenseExists.get());

                List<ExpenseResponse> expenseResponse = new ArrayList<>();

                for(Expense b : checkIfExpenseExists.get()) {
                    ExpenseResponse br = new ExpenseResponse();
                    BeanUtils.copyProperties(b, br);
                    expenseResponse.add(br);
                }
                return expenseResponse;
        } else {
            throw new ExpenseCustomException("Expense not found for User Id: " + userId);
        }
    }

    /*
        Usage: Method to log an expense to DB.
    */
    @Transactional
    @CacheEvict(value = "expense-service", allEntries = true)
    public ExpenseResponse logExpense(ExpenseRequest expenseRequest) {

        Expense expense = Expense
                .builder()
                .amount(expenseRequest.getAmount())
                .category(expenseRequest.getCategory())
                .description(expenseRequest.getDescription())
                .userId(expenseRequest.getUserId())
                .build();

        log.info("Creating Expense::::");
        expense = expenseRepository.save(expense);

        ExpenseResponse expenseResponse = new ExpenseResponse();
        BeanUtils.copyProperties(expense, expenseResponse);

        return expenseResponse;
    }


    /*
        Usage: Method to update the expense.
    */
    @Transactional
    @CacheEvict(value = "expense-service", allEntries = true)
    public ExpenseResponse updateExpense(ExpenseRequest expense) {

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

        ExpenseResponse expenseResponse = new ExpenseResponse();
        BeanUtils.copyProperties(existingExpense, expenseResponse);

        return expenseResponse;
    }

    /*
        Usage: Method to delete the expense.
    */
    @Transactional
    @CacheEvict(value = "expense-service", allEntries = true)
    public void deleteExpense(Long id) {
        log.info("Inside delete expense Method::");
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
