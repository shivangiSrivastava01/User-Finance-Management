package com.finance.budgetservice.controller;

import com.finance.budgetservice.client.UserClient;
import com.finance.budgetservice.dto.UserDTO;
import com.finance.budgetservice.exception.BudgetCustomException;
import com.finance.budgetservice.model.Budget;
import com.finance.budgetservice.service.BudgetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/financeManagement")
@Tag(name = "Budget Management System", description = "Operations pertaining to budget in Budget Management System")
public class BudgetController {


    private final BudgetService budgetService;
    private final UserClient userClient;

    @Autowired
    public BudgetController(BudgetService budgetService,UserClient userClient) {
        this.budgetService = budgetService;
        this.userClient = userClient;

    }

    /*
    Usage: This API fetches all the budget for a particular user.
    */
    @Operation(summary = "View a list of available budgets with respect to UserID")
    @GetMapping("/budgets/{userId}")
    public ResponseEntity<List<Budget>> getUserBudget(@PathVariable Long userId) {
        try {
            Optional<List<Budget>> budgets = budgetService.getUserSpecificBudget(userId);
            if (budgets.isPresent() && !budgets.get().isEmpty()) {

                log.info("Budget for userID: {} is Found!!!!!!",userId);
                return new ResponseEntity<>(budgets.get(), HttpStatus.OK);
            } else {
                log.info("Budget Not Found for userID: {} Please check!!!!!!",userId);
                throw new BudgetCustomException("Budget not found for User Id: " + userId);
            }
        } catch (BudgetCustomException e) {
            log.error("Budget Not Found for userID: {}",userId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Exception occurred while retrieving the budget data with respect to UserID: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /*
    Usage: This API fetches budget with respect to Budget id.
    */
    @Operation(summary = "View budget with respect to Budget id")
    @GetMapping("/{budgetId}")
    public ResponseEntity<Budget> getBudgetWithId(@PathVariable Long budgetId) {
        try {
            Optional<Budget> budget = budgetService.getBudgetFromId(budgetId);
            if (budget.isPresent()) {
                log.info("Budget Found for user with respect to budgetID: {}", budgetId);
                return new ResponseEntity<>(budget.get(), HttpStatus.OK);
            } else {
                throw new BudgetCustomException("Budget not found for Budget Id: " + budgetId);
            }
        } catch (BudgetCustomException e) {
            log.error("Budget Not Found for user with budgetID: {} Please check!!!!!!", budgetId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Exception occurred while retrieving the budget data with respect to budgetID: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /*
       Usage: This API saves a Budget specific to a user.
    */

    @Operation(summary = "Add a budget")
    @PostMapping("/budgetCreation")
    public ResponseEntity<String> createBudget(@Valid @RequestBody Budget budget) {
        try {
            UserDTO userData =  userClient.getUser(budget.getUserId());

            //checking here, if user for which budget will be created exists in db.
            if(userData==null || userData.getEmail()==null){
                throw new BudgetCustomException("User do not exists in DB::");
            }

            log.info("Budget creation starts:::");
            Budget budgetData = budgetService.createBudget(budget);

            if(budgetData==null){
                throw new BudgetCustomException("Budget do not exists in DB::");
            }
            return new ResponseEntity<>("Budget created successfully", HttpStatus.CREATED);
        } catch (BudgetCustomException e) {
            log.info("Exception occurred while creating the budget data: {}", e.getMessage());
            return new ResponseEntity<>("Error creating budget: " + e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    /*
       Usage: This API updates budget.
    */
    @Operation(summary = "Update a budget")
    @PutMapping("/budgetUpdate")
    public ResponseEntity<String> updateBudget(@Valid @RequestBody Budget budget) {
        try {
            budgetService.updateBudget(budget);
            return new ResponseEntity<>("Budget updated successfully", HttpStatus.OK);
        } catch (BudgetCustomException e) {
            log.error("Exception occurred while updating the budget data: {}", e.getMessage());
            return new ResponseEntity<>("Error updating budget: " + e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    /*
       Usage: This API deletes the budget with respect to user and category.
    */
    @Operation(summary = "Delete a budget")
    @DeleteMapping("/budgetDeletion/{id}")
    public ResponseEntity<String> deleteBudget(@PathVariable Long id) {
        try {
            budgetService.deleteBudget(id);
            return new ResponseEntity<>("Budget deleted successfully", HttpStatus.OK);
        } catch (BudgetCustomException e) {
            log.error("Exception occurred while deleting the budget data: {}", e.getMessage());
            return new ResponseEntity<>("Error deleting budget: " + e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    /*
       Usage: This API fetches the budget with respect to a specific user and category.
    */
    @Operation(summary = "Get Category budget by userid")
    @GetMapping("/userCategoryBudget")
    public ResponseEntity<Budget> getUserBudgetSpecificToCategory(@RequestParam Long userId, @RequestParam String category) {
        try {
            Budget budget = budgetService.getUserCategoryBudget(userId, category.toLowerCase());
            return new ResponseEntity<>(budget, HttpStatus.OK);
        } catch (BudgetCustomException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Exception occurred while getting the budget data: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
