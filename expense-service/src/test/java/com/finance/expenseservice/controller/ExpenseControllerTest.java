package com.finance.expenseservice.controller;

import com.finance.expenseservice.client.BudgetClient;
import com.finance.expenseservice.client.UserClient;
import com.finance.expenseservice.dto.BudgetDTO;
import com.finance.expenseservice.dto.UserDTO;
import com.finance.expenseservice.exception.ExpenseCustomException;
import com.finance.expenseservice.model.ExpenseRequest;
import com.finance.expenseservice.model.ExpenseResponse;
import com.finance.expenseservice.service.ExpenseServiceImpl;
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
    private ExpenseServiceImpl expenseServiceImpl;

    @Mock
    private BudgetClient budgetClient;

    @Mock
    private UserClient userClient;

    @InjectMocks
    private ExpenseController expenseController;

    private UserDTO userDTO;
    private BudgetDTO budgetDTO;

    private ExpenseRequest expenseRequest;

    private ExpenseResponse expenseResponse;

    @BeforeEach
    void setUp() {

        userDTO = new UserDTO();
        userDTO.setUserId(1L);
        userDTO.setName("John Doe");
        userDTO.setEmail("john.doe@example.com");

        budgetDTO = new BudgetDTO();
        budgetDTO.setId(1L);
        budgetDTO.setUserId(1L);
        budgetDTO.setCategory("Food");
        budgetDTO.setAmount(500);

        expenseRequest = new ExpenseRequest();
        expenseRequest.setUserId(1L);

        expenseResponse = new ExpenseResponse();
        expenseResponse.setUserId(1L);

}
    @Test
    void testGetUserExpense_Success() {

        when(expenseServiceImpl.getUserSpecificExpense(1L)).thenReturn(Optional.of(List.of(expenseResponse)));

        ResponseEntity<List<ExpenseResponse>> response = expenseController.getUserExpense(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, Objects.requireNonNull(response.getBody()).size());
        assertEquals(expenseResponse, response.getBody().get(0));
    }

    @Test
    void testGetUserExpense_NotFound() {
        when(expenseServiceImpl.getUserSpecificExpense(1L)).thenReturn(Optional.empty());

        ResponseEntity<List<ExpenseResponse>> response = expenseController.getUserExpense(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetExpenseWithId_Success() {

        when(expenseServiceImpl.getExpenseWithId(1L)).thenReturn(Optional.of(expenseResponse));

        ResponseEntity<ExpenseResponse> response = expenseController.getExpenseWithId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expenseResponse, response.getBody());
    }

    @Test
    void testGetExpenseWithId_NotFound() {
        when(expenseServiceImpl.getExpenseWithId(1L)).thenReturn(Optional.empty());

        ResponseEntity<ExpenseResponse> response = expenseController.getExpenseWithId(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetUserExpenseSpecificToUserCategory_Success() {

        when(expenseServiceImpl.getUserCategoryExpense(1L, "Food")).thenReturn(List.of(expenseResponse));

        ResponseEntity<Object> response = expenseController.getUserExpenseSpecificToUserCategory(1L, "Food");

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetUserExpenseSpecificToUserCategory_NotFound() {

        when(expenseServiceImpl.getUserCategoryExpense(1L, "Food")).thenThrow(new ExpenseCustomException("Expense not found for User Id: " + '1'));

        ResponseEntity<Object> response = expenseController.getUserExpenseSpecificToUserCategory(1L, "Food");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Expense for user with ID 1 and category Food not found.", response.getBody());
    }

    @Test
    void testLogExpense_Success() {

        when(userClient.getUser(1L)).thenReturn(userDTO);
        when(budgetClient.getBudget(any(ExpenseResponse.class))).thenReturn(budgetDTO);
        when(expenseServiceImpl.logExpense(any(ExpenseRequest.class))).thenReturn(expenseResponse);

        ResponseEntity<String> response = expenseController.logExpense(expenseRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("expense created successfully", response.getBody());
    }

    @Test
    void testLogExpense_UserNotFound() {
        lenient().when(userClient.getUser(1L)).thenReturn(userDTO);
        lenient().when(budgetClient.getBudget(any(ExpenseResponse.class))).thenReturn(budgetDTO);
        lenient().when(expenseServiceImpl.logExpense(any(ExpenseRequest.class))).thenThrow(new ExpenseCustomException("User do not exists in DB"));

        ResponseEntity<String> response = expenseController.logExpense(expenseRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Error creating expense: User do not exists in DB", response.getBody());

    }

    @Test
    void testUpdateExpense_Success() {

        when(expenseServiceImpl.updateExpense(any(ExpenseRequest.class))).thenReturn(expenseResponse);
        when(userClient.getUser(1L)).thenReturn(userDTO);
        when(budgetClient.getBudget(any(ExpenseResponse.class))).thenReturn(budgetDTO);

        ResponseEntity<String> response = expenseController.updateExpense(expenseRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Expense updated successfully", response.getBody());
    }

    @Test
    void testUpdateExpense_UserNotFound() {
        lenient().when(expenseServiceImpl.updateExpense(any(ExpenseRequest.class))).thenReturn(expenseResponse);
        lenient().when(userClient.getUser(1L)).thenReturn(new UserDTO());

        ResponseEntity<String> response = expenseController.updateExpense(expenseRequest);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void testDeleteExpense_Success() {
        doNothing().when(expenseServiceImpl).deleteExpense(1L);

        ResponseEntity<String> response = expenseController.deleteExpense(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Expense deleted successfully", response.getBody());
    }

    @Test
    void testDeleteExpense_Error() {
        doThrow(new RuntimeException("Deletion error")).when(expenseServiceImpl).deleteExpense(1L);

        ResponseEntity<String> response = expenseController.deleteExpense(1L);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error deleting expense: Deletion error", response.getBody());
    }
}
