package com.finance.budgetservice.controller;

import com.finance.budgetservice.client.UserClient;
import com.finance.budgetservice.dto.UserDTO;
import com.finance.budgetservice.exception.BudgetCustomException;
import com.finance.budgetservice.model.BudgetRequest;
import com.finance.budgetservice.model.BudgetResponse;
import com.finance.budgetservice.service.BudgetServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BudgetControllerTest {

    @Mock
    private BudgetServiceImpl budgetServiceImpl;

    @Mock
    private UserClient userClient;

    @InjectMocks
    private BudgetController budgetController;

    private BudgetRequest budgetRequest;
    private UserDTO userDTO;

    @BeforeEach
    void setUp() {

        budgetRequest = new BudgetRequest();
        budgetRequest.setUserId(1L);

        userDTO = new UserDTO();
        userDTO.setEmail("test@example.com");
    }

    @Test
    void getUserBudget_success() {
        List<BudgetResponse> budgets = List.of(new BudgetResponse());
        when(budgetServiceImpl.getUserSpecificBudget(anyLong())).thenReturn(Optional.of(budgets));

        ResponseEntity<List<BudgetResponse>> response = budgetController.getUserBudget(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(budgets, response.getBody());
    }

    @Test
    void getUserBudget_exception() {
        when(budgetServiceImpl.getUserSpecificBudget(anyLong())).thenThrow(new RuntimeException());

        ResponseEntity<List<BudgetResponse>> response = budgetController.getUserBudget(1L);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void getBudgetWithId_success() {
        BudgetResponse budget = new BudgetResponse();

        when(budgetServiceImpl.getBudgetFromId(anyLong())).thenReturn(Optional.of(budget));

        ResponseEntity<BudgetResponse> response = budgetController.getBudgetWithId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(budget, response.getBody());
    }

    @Test
    void getBudgetWithId_exception() {
        when(budgetServiceImpl.getBudgetFromId(anyLong())).thenThrow(new RuntimeException());

        ResponseEntity<BudgetResponse> response = budgetController.getBudgetWithId(1L);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void createBudget_success() {

        when(userClient.getUser(anyLong())).thenReturn(userDTO);

        BudgetResponse budgetResponse = new BudgetResponse();
        BeanUtils.copyProperties(budgetRequest, budgetResponse);

        when(budgetServiceImpl.createBudget(budgetRequest)).thenReturn(budgetResponse);

        ResponseEntity<String> response = budgetController.createBudget(budgetRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Budget created successfully", response.getBody());
    }

    @Test
    void createBudget_exception() {

        lenient().when(budgetServiceImpl.createBudget(budgetRequest)).thenThrow(new BudgetCustomException("Error Creating Budget"));

        ResponseEntity<String> response = budgetController.createBudget(budgetRequest);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void updateBudget_success() {

        ResponseEntity<String> response = budgetController.updateBudget(budgetRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Budget updated successfully", response.getBody());
    }

    @Test
    void updateBudget_exception() {

        doThrow(new BudgetCustomException("Error updating budget")).when(budgetServiceImpl).updateBudget(any(BudgetRequest.class));

        ResponseEntity<String> response = budgetController.updateBudget(budgetRequest);

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
        doThrow(new BudgetCustomException("Error deleting budget")).when(budgetServiceImpl).deleteBudget(anyLong());

        ResponseEntity<String> response = budgetController.deleteBudget(1L);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Error deleting budget: Error deleting budget", response.getBody());
    }

    @Test
    void getUserBudgetSpecificToCategory_success() {

        BudgetResponse budgetResponse = new BudgetResponse();
        when(budgetServiceImpl.getUserCategoryBudget(anyLong(), anyString())).thenReturn(budgetResponse);

        ResponseEntity<BudgetResponse> response = budgetController.getUserBudgetSpecificToCategory(1L, "category");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(budgetResponse, response.getBody());
    }

    @Test
    void getUserBudgetSpecificToCategory_exception() {
        when(budgetServiceImpl.getUserCategoryBudget(anyLong(), anyString())).thenThrow(new RuntimeException());

        ResponseEntity<BudgetResponse> response = budgetController.getUserBudgetSpecificToCategory(1L, "category");

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
