package com.finance.expenseservice.controller;

import com.finance.expenseservice.client.BudgetClient;
import com.finance.expenseservice.client.UserClient;
import com.finance.expenseservice.dto.UserDTO;
import com.finance.expenseservice.exception.ExpenseCustomException;
import com.finance.expenseservice.dto.BudgetDTO;
import com.finance.expenseservice.model.Expense;
import com.finance.expenseservice.service.ExpenseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@Slf4j
@RequestMapping("/financeManagement")
@Tag(name = "Expense Service", description = "Operations pertaining to expense in Finance Management System")
public class ExpenseController {


    private final ExpenseService expenseService;
    private final BudgetClient expenseClient;
    private final RestTemplate restTemplate;
    private final UserClient userClient;

    @Autowired
    public ExpenseController(ExpenseService expenseService, BudgetClient expenseClient, RestTemplate restTemplate, UserClient userClient) {
        this.expenseService = expenseService;
        this.expenseClient = expenseClient;
        this.restTemplate = restTemplate;
        this.userClient = userClient;
    }

    /*
        Usage: This API fetches the expense with respect to user id.
    */
    @Operation(summary = "View a list of available expense log")
    @GetMapping("/expense/{userId}")
    public ResponseEntity<List<Expense>> getUserExpense(@PathVariable Long userId) {
        try {
            Optional<List<Expense>> expense = expenseService.getUserSpecificExpense(userId);
            if (expense.isPresent() && !expense.get().isEmpty()) {
                log.info("Expense for userID: {} is Found!!!!!!",userId);
                return new ResponseEntity<>(expense.get(), HttpStatus.OK);
            } else {
                throw new ExpenseCustomException("Expense not found for User Id: " + userId);
            }
        } catch (ExpenseCustomException e) {
            log.error("Expense Not Found for userID: {}",userId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Exception occurred while retrieving the expense data: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /*
        Usage: This API fetches the expense with respect to expense id.
    */

    @Operation(summary = "View expense with respect to Expense id")
    @GetMapping("/{expenseId}")
    public ResponseEntity<Expense> getExpenseWithId(@PathVariable Long expenseId) {
        try {
            Optional<Expense> expense = expenseService.getExpenseWithId(expenseId);
            if (expense.isPresent()) {

                log.info("Expense Found for user with respect to expenseID: {}", expenseId);
                return new ResponseEntity<>(expense.get(), HttpStatus.OK);
            } else {
                throw new ExpenseCustomException("Expense not found for Expense Id: " + expenseId);
            }
        } catch (ExpenseCustomException e) {
            log.error("Expense Not Found for this with expenseID: {} Please check!!!!!!", expenseId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Exception occurred while retrieving the expense data with respect to expenseID: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /*
        Usage: This API fetches the expense with respect to specific user id and category.
    */

    @Operation(summary = "Get Category expense by userid and category")
    @GetMapping("/userCategoryExpense")
    public ResponseEntity<Object> getUserExpenseSpecificToUserCategory(@RequestParam Long userId, @RequestParam String category) {
        try {
            List<Expense> expenseList = expenseService.getUserCategoryExpense(userId, category);
            return new ResponseEntity<>(expenseList, HttpStatus.OK);
        } catch (ExpenseCustomException e) {
            String errorMessage = "Expense for user with ID " + userId + " and category " + category + " not found.";
            return new ResponseEntity<>(errorMessage, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Exception occurred while getting the expense data: {}", e.getMessage());
            String errorMessage = "An error occurred while processing your request. Please try again later.";
            return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /*
        Usage: This API creates a new expense with respect to user id.
    */
    @Operation(summary = "Log an Expense")
    @PostMapping("/expenseCreation")
    public ResponseEntity<String> logExpense(@Valid @RequestBody Expense expense) {
        try {

            UserDTO userData =  userClient.getUser(expense.getUserId());

            //checking here, if user for which expense will be logged exists in db.
            if(userData.getEmail()==null){
                throw new ExpenseCustomException("User do not exists in DB");
            }

            log.info("Expense creation for user starts::");
            Expense expenseData = expenseService.logExpense(expense);

            if(expenseData==null){
                throw new ExpenseCustomException("Expense Not Found!!!!!");
            }

            //check if expenseTotalAmount>budgetAmount for a category
            String message = checkIfExpenseTotalAmountExceedsBudget(userData,expenseData);
            log.info(message);

            if(!Objects.equals(message, "")){
                return new ResponseEntity<>(message, HttpStatus.OK);
            }

            return new ResponseEntity<>("expense created successfully", HttpStatus.CREATED);

        } catch(ExpenseCustomException ex){
            log.error("Error creating expense: {}", ex.getMessage());
            return new ResponseEntity<>("Error creating expense: " + ex.getMessage(), HttpStatus.NOT_FOUND);
        }
        catch (Exception e) {
            log.error("Exception occurred while creating the expense data: {}", e.getMessage());
            return new ResponseEntity<>("Error creating expense: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String checkIfExpenseTotalAmountExceedsBudget(UserDTO userData,Expense expenseData) {

        log.info("checkIfExpenseTotalAmountExceedsBudget method starts::");

        //Fetching budget data to send it in notification service call, for budgetAmount and budgetCategory
        BudgetDTO budget =  expenseClient.getBudget(expenseData);

        double budgetAmount = budget.getAmount();
        String message = "";

        double categoryExpenseTotalAmount = expenseService.getCategoryExpenseTotalAmount(expenseData.getUserId(),expenseData.getCategory());

        //here we check if the expense total amount exceeds the budget amount then the notification service call happens
        if(categoryExpenseTotalAmount>budgetAmount){

            log.info("Calling notification service as the expenseTotalAmount exceeds the budgetAmount::");
            String notificationServiceUrl = UriComponentsBuilder.fromHttpUrl("http://localhost:8082/financeManagement/notifyUser")
                    .queryParam("budgetCategory", budget.getCategory())
                    .queryParam("budgetAmount", budget.getAmount())
                    .queryParam("expenseDescription", expenseData.getDescription())
                    .queryParam("expenseAmount", categoryExpenseTotalAmount)
                    .queryParam("userEmail", userData.getEmail())
                    .toUriString();

            message = restTemplate.getForObject(notificationServiceUrl, String.class);
        }
        log.info("Message Retrieved:: {}",message);
        return message;
    }


    /*
        Usage: This API updates the expense.
    */
    @Operation(summary = "Update a expense")
    @PutMapping("/expenseUpdate")
    public ResponseEntity<String> updateExpense(@Valid @RequestBody Expense expense) {
        try {

            UserDTO userData =  userClient.getUser(expense.getUserId());

            //checking here, if user for which expense will be updated exists in db.
            if(userData.getEmail()==null){
                throw new ExpenseCustomException("User do not exists in DB");
            }

            //expense should be updated and then call notification service if expenseAmount exceeds budgetAmount
            Expense expenseData = expenseService.updateExpense(expense);

            //check if expenseTotalAmount>budgetAmount for a category
            String message = checkIfExpenseTotalAmountExceedsBudget(userData,expenseData);
            log.info(message);

            if(!Objects.equals(message, "")){
                return new ResponseEntity<>(message, HttpStatus.OK);
            }

            return new ResponseEntity<>("Expense updated successfully", HttpStatus.OK);
        }
        catch (ExpenseCustomException e) {
            log.error("Exception occurred while logging the expense data: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }catch (Exception e) {
            log.error("Exception occurred while updating the expense data: {}", e.getMessage());
            return new ResponseEntity<>("Error updating expense: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /*
        Usage: This API deletes the expense with respect to its id.
    */
    @Operation(summary = "Delete an expense")
    @DeleteMapping("/expenseDeletion/{id}")
    public ResponseEntity<String> deleteExpense(@PathVariable Long id) {
        try {
            expenseService.deleteExpense(id);
            return new ResponseEntity<>("Expense deleted successfully", HttpStatus.OK);
        } catch (ExpenseCustomException e) {
            log.error("Exception occurred while deleting the expense: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }catch (Exception e) {
            log.error("Exception occurred while deleting the expense data: {}", e.getMessage());
            return new ResponseEntity<>("Error deleting expense: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
