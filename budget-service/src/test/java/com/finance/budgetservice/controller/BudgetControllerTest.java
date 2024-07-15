package com.finance.budgetservice.controller;

import com.finance.budgetservice.client.UserClient;
import com.finance.budgetservice.dto.UserDTO;
import com.finance.budgetservice.exception.BudgetCustomException;
import com.finance.budgetservice.model.Budget;
import com.finance.budgetservice.service.BudgetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class BudgetControllerTest {

    @Mock
    private BudgetService budgetService;

    @Mock
    private UserClient userClient;

    @InjectMocks
    private BudgetController budgetController;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getUserBudget_success() {
        List<Budget> budgets = List.of(new Budget());
        when(budgetService.getUserSpecificBudget(anyLong())).thenReturn(Optional.of(budgets));

        ResponseEntity<List<Budget>> response = budgetController.getUserBudget(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(budgets, response.getBody());
    }

    @Test
    void getUserBudget_exception() {
        when(budgetService.getUserSpecificBudget(anyLong())).thenThrow(new RuntimeException());

        ResponseEntity<List<Budget>> response = budgetController.getUserBudget(1L);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void getBudgetWithId_success() {
        Budget budget = new Budget();
        when(budgetService.getBudgetFromId(anyLong())).thenReturn(Optional.of(budget));

        ResponseEntity<Budget> response = budgetController.getBudgetWithId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(budget, response.getBody());
    }

    @Test
    void getBudgetWithId_exception() {
        when(budgetService.getBudgetFromId(anyLong())).thenThrow(new RuntimeException());

        ResponseEntity<Budget> response = budgetController.getBudgetWithId(1L);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void createBudget_success() {
        Budget budget = new Budget();
        budget.setUserId(1L);
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("test@example.com");

        when(userClient.getUser(anyLong())).thenReturn(userDTO);
        when(budgetService.createBudget(budget)).thenReturn(budget);

        ResponseEntity<String> response = budgetController.createBudget(budget);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Budget created successfully", response.getBody());
    }

    @Test
    void createBudget_exception() {
        Budget budget = new Budget();
        budget.setUserId(1L);

        when(budgetService.createBudget(budget)).thenThrow(new BudgetCustomException("Error Creating Budget"));

        ResponseEntity<String> response = budgetController.createBudget(budget);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void updateBudget_success() {
        Budget budget = new Budget();

        ResponseEntity<String> response = budgetController.updateBudget(budget);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Budget updated successfully", response.getBody());
    }

    @Test
    void updateBudget_exception() {
        Budget budget = new Budget();
        doThrow(new BudgetCustomException("Error updating budget")).when(budgetService).updateBudget(any(Budget.class));

        ResponseEntity<String> response = budgetController.updateBudget(budget);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Error updating budget: Error updating budget", response.getBody());
    }

    @Test
    void deleteBudget_success() {
        ResponseEntity<String> response = budgetController.deleteBudget(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Budget deleted successfully", response.getBody());
    }

    @Test
    void deleteBudget_exception() {
        doThrow(new BudgetCustomException("Error deleting budget")).when(budgetService).deleteBudget(anyLong());

        ResponseEntity<String> response = budgetController.deleteBudget(1L);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Error deleting budget: Error deleting budget", response.getBody());
    }

    @Test
    void getUserBudgetSpecificToCategory_success() {
        Budget budget = new Budget();
        when(budgetService.getUserCategoryBudget(anyLong(), anyString())).thenReturn(budget);

        ResponseEntity<Budget> response = budgetController.getUserBudgetSpecificToCategory(1L, "category");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(budget, response.getBody());
    }

    @Test
    void getUserBudgetSpecificToCategory_exception() {
        when(budgetService.getUserCategoryBudget(anyLong(), anyString())).thenThrow(new RuntimeException());

        ResponseEntity<Budget> response = budgetController.getUserBudgetSpecificToCategory(1L, "category");

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
