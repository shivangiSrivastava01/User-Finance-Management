package com.finance.budgetservice.service;

import com.finance.budgetservice.exception.BudgetCustomException;
import com.finance.budgetservice.model.Budget;
import com.finance.budgetservice.model.BudgetRequest;
import com.finance.budgetservice.model.BudgetResponse;
import com.finance.budgetservice.repository.BudgetRepository;
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
public class BudgetServiceImpl implements BudgetService{

    private final BudgetRepository budgetRepository;
    private static final  String NOT_FOUND_ERROR_MESSAGE = "Budget does not exist with Id: ";

    @Autowired
    public BudgetServiceImpl(BudgetRepository budgetRepository) {
        this.budgetRepository = budgetRepository;
    }

    /*
       Usage: Method to fetch all the budget for a particular user.
    */
    @Cacheable(value = "budget-service")
    public Optional<List<BudgetResponse>> getUserSpecificBudget(Long userId) {
        log.info("Fetch budget specific to userID::");
        Optional<List<Budget>> budget = budgetRepository.findByUserId(userId);

        if(budget.isPresent() && !budget.get().isEmpty()) {
            List<BudgetResponse> budgetResponse = new ArrayList<>();

            for(Budget b : budget.get()) {
                BudgetResponse br = new BudgetResponse();
                BeanUtils.copyProperties(b, br);
                budgetResponse.add(br);
            }

            return Optional.of(budgetResponse);
        }
        return Optional.empty();
    }

    /*
       Usage: Method to fetch all the budget for a particular budgetId.
    */

    public Optional<BudgetResponse> getBudgetFromId(Long budgetId) {

        Optional<Budget> budgetOptional = budgetRepository.findById(budgetId);

        if (budgetOptional.isPresent()) {
            BudgetResponse budgetResponse = new BudgetResponse();
            Budget budget = budgetOptional.get();
            BeanUtils.copyProperties(budget, budgetResponse);

            return Optional.of(budgetResponse);
        }

        return Optional.empty();
    }

    /*
       Usage: Method to save a new budget for a user.
    */
    @Transactional
    @CacheEvict(value = "budget-service", allEntries = true)
    public BudgetResponse createBudget(BudgetRequest budgetRequest) {

        if (budgetRepository.findBudgetByUserCategory(budgetRequest.getUserId(),budgetRequest.getCategory()).isPresent()) {
            throw new BudgetCustomException("Budget already exists!!!!");
        }

        Budget budget = Budget
                .builder()
                .amount(budgetRequest.getAmount())
                .category(budgetRequest.getCategory())
                .userId(budgetRequest.getUserId())
                .build();

        log.info("Creating Budget::::");
        budget = budgetRepository.save(budget);

        BudgetResponse budgetResponse = new BudgetResponse();
        BeanUtils.copyProperties(budget, budgetResponse);

        return budgetResponse;

    }

    /*
       Usage: Method to update the budget.
    */
    @Transactional
    @CacheEvict(value = "budget-service", allEntries = true)
    public void updateBudget(BudgetRequest budgetRequest) {

        log.info("Inside update budget method::");
        Optional<Budget> checkIfBudgetExists = budgetRepository.findById(budgetRequest.getId());
        if (checkIfBudgetExists.isEmpty()) {
            throw new BudgetCustomException(NOT_FOUND_ERROR_MESSAGE + budgetRequest.getId());
        }

        Budget existingBudget = checkIfBudgetExists.get();

        if(budgetRequest.getCategory()!=null) {
            existingBudget.setCategory(budgetRequest.getCategory());
        }
        if (budgetRequest.getAmount()>0) {
            existingBudget.setAmount(budgetRequest.getAmount());
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
    public BudgetResponse getUserCategoryBudget(Long userId, String category) {

        Optional<Budget> checkIfBudgetExists = budgetRepository.findBudgetByUserCategory(userId, category);

        if (checkIfBudgetExists.isEmpty()) {
            throw new BudgetCustomException(NOT_FOUND_ERROR_MESSAGE + userId);
        }
        log.info("Get Budget by userID and Category:: {}", checkIfBudgetExists.get());


        BudgetResponse budgetResponse = new BudgetResponse();
        BeanUtils.copyProperties(checkIfBudgetExists.get(), budgetResponse);

        return budgetResponse;
    }
}
