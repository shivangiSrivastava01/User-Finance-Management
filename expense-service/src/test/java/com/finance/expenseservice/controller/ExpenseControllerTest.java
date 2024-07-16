package com.finance.expenseservice.controller;

import com.finance.expenseservice.client.BudgetClient;
import com.finance.expenseservice.client.UserClient;
import com.finance.expenseservice.dto.BudgetDTO;
import com.finance.expenseservice.dto.UserDTO;
import com.finance.expenseservice.exception.ExpenseCustomException;
import com.finance.expenseservice.model.Expense;
import com.finance.expenseservice.service.ExpenseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseControllerTest {

    @Mock
    private ExpenseService expenseService;

    @Mock
    private BudgetClient budgetClient;

    @Mock
    private UserClient userClient;

    @InjectMocks
    private ExpenseController expenseController;

    private Expense expense;
    private UserDTO userDTO;
    private BudgetDTO budgetDTO;

    @BeforeEach
    void setUp() {
        expense = new Expense();
        expense.setId(1L);
        expense.setUserId(1L);
        expense.setCategory("Food");
        expense.setAmount(100);
        expense.setDescription("Grocery shopping");

        userDTO = new UserDTO();
        userDTO.setUserId(1L);
        userDTO.setName("John Doe");
        userDTO.setEmail("john.doe@example.com");

        budgetDTO = new BudgetDTO();
        budgetDTO.setId(1L);
        budgetDTO.setUserId(1L);
        budgetDTO.setCategory("Food");
        budgetDTO.setAmount(500);

}
    @Test
    void testGetUserExpense_Success() {
        when(expenseService.getUserSpecificExpense(1L)).thenReturn(Optional.of(List.of(expense)));

        ResponseEntity<List<Expense>> response = expenseController.getUserExpense(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, Objects.requireNonNull(response.getBody()).size());
        assertEquals(expense, response.getBody().get(0));
    }

    @Test
    void testGetUserExpense_NotFound() {
        when(expenseService.getUserSpecificExpense(1L)).thenReturn(Optional.empty());

        ResponseEntity<List<Expense>> response = expenseController.getUserExpense(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetExpenseWithId_Success() {
        when(expenseService.getExpenseWithId(1L)).thenReturn(Optional.of(expense));

        ResponseEntity<Expense> response = expenseController.getExpenseWithId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expense, response.getBody());
    }

    @Test
    void testGetExpenseWithId_NotFound() {
        when(expenseService.getExpenseWithId(1L)).thenReturn(Optional.empty());

        ResponseEntity<Expense> response = expenseController.getExpenseWithId(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetUserExpenseSpecificToUserCategory_Success() {
        when(expenseService.getUserCategoryExpense(1L, "Food")).thenReturn(List.of(expense));

        ResponseEntity<Object> response = expenseController.getUserExpenseSpecificToUserCategory(1L, "Food");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, ((List<?>) Objects.requireNonNull(response.getBody())).size());
        assertEquals(expense, ((List<?>) response.getBody()).get(0));
    }

    @Test
    void testGetUserExpenseSpecificToUserCategory_NotFound() {

        when(expenseService.getUserCategoryExpense(1L, "Food")).thenThrow(new ExpenseCustomException("Expense not found for User Id: " + '1'));

        ResponseEntity<Object> response = expenseController.getUserExpenseSpecificToUserCategory(1L, "Food");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Expense for user with ID 1 and category Food not found.", response.getBody());
    }

    @Test
    void testLogExpense_Success() {
        when(userClient.getUser(1L)).thenReturn(userDTO);
        when(budgetClient.getBudget(any(Expense.class))).thenReturn(budgetDTO);
        when(expenseService.logExpense(any(Expense.class))).thenReturn(expense);

        ResponseEntity<String> response = expenseController.logExpense(expense);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("expense created successfully", response.getBody());
    }

    @Test
    void testLogExpense_UserNotFound() {
        lenient().when(userClient.getUser(1L)).thenReturn(userDTO);
        lenient().when(budgetClient.getBudget(any(Expense.class))).thenReturn(budgetDTO);
        lenient().when(expenseService.logExpense(any(Expense.class))).thenThrow(new ExpenseCustomException("User do not exists in DB"));

        ResponseEntity<String> response = expenseController.logExpense(expense);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Error creating expense: User do not exists in DB", response.getBody());

    }

    @Test
    void testUpdateExpense_Success() {
        when(expenseService.updateExpense(any(Expense.class))).thenReturn(expense);
        when(userClient.getUser(1L)).thenReturn(userDTO);
        when(budgetClient.getBudget(any(Expense.class))).thenReturn(budgetDTO);

        ResponseEntity<String> response = expenseController.updateExpense(expense);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Expense updated successfully", response.getBody());
    }

    @Test
    void testUpdateExpense_UserNotFound() {
        when(expenseService.updateExpense(any(Expense.class))).thenReturn(expense);
        when(userClient.getUser(1L)).thenReturn(new UserDTO());

        ResponseEntity<String> response = expenseController.updateExpense(expense);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void testDeleteExpense_Success() {
        doNothing().when(expenseService).deleteExpense(1L);

        ResponseEntity<String> response = expenseController.deleteExpense(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Expense deleted successfully", response.getBody());
    }

    @Test
    void testDeleteExpense_Error() {
        doThrow(new RuntimeException("Deletion error")).when(expenseService).deleteExpense(1L);

        ResponseEntity<String> response = expenseController.deleteExpense(1L);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error deleting expense: Deletion error", response.getBody());
    }
}
