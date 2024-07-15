package com.finance.expenseservice.service;

import com.finance.expenseservice.exception.ExpenseCustomException;
import com.finance.expenseservice.model.Expense;
import com.finance.expenseservice.repository.ExpenseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @InjectMocks
    private ExpenseService expenseService;

    private Expense expense;

    @BeforeEach
    void setUp() {
        expense = new Expense();
        expense.setId(1L);
        expense.setUserId(1L);
        expense.setCategory("Food");
        expense.setAmount(100);
        expense.setDescription("Grocery shopping");
    }

    @Test
    void testGetUserSpecificExpense_Success() {
        when(expenseRepository.findByUserId(1L)).thenReturn(Optional.of(List.of(expense)));

        Optional<List<Expense>> result = expenseService.getUserSpecificExpense(1L);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().size());
        assertEquals(expense, result.get().get(0));
    }

    @Test
    void testGetUserSpecificExpense_NotFound() {
        when(expenseRepository.findByUserId(1L)).thenReturn(Optional.empty());

        Optional<List<Expense>> result = expenseService.getUserSpecificExpense(1L);

        assertFalse(result.isPresent());
    }

    @Test
    void testGetExpenseWithId_Success() {
        when(expenseRepository.findById(1L)).thenReturn(Optional.of(expense));

        Optional<Expense> result = expenseService.getExpenseWithId(1L);

        assertTrue(result.isPresent());
        assertEquals(expense, result.get());
    }

    @Test
    void testGetExpenseWithId_NotFound() {
        when(expenseRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Expense> result = expenseService.getExpenseWithId(1L);

        assertFalse(result.isPresent());
    }

    @Test
    void testGetUserCategoryExpense_Success() {
        when(expenseRepository.findExpenseByUserCategory(1L, "food")).thenReturn(Optional.of(List.of(expense)));

        List<Expense> result = expenseService.getUserCategoryExpense(1L, "Food");

        assertEquals(1, result.size());
        assertEquals(expense, result.get(0));
    }

    @Test
    void testGetUserCategoryExpense_NotFound() {
        when(expenseRepository.findExpenseByUserCategory(1L, "food")).thenReturn(Optional.empty());

        ExpenseCustomException exception = assertThrows(ExpenseCustomException.class, () -> {
            expenseService.getUserCategoryExpense(1L, "Food");
        });

        assertEquals("Expense not found for User Id: 1", exception.getMessage());
    }

    @Test
    void testUpdateExpense_Success() {
        Expense updatedExpense = new Expense();
        updatedExpense.setId(1L);
        updatedExpense.setUserId(1L);
        updatedExpense.setCategory("Travel");
        updatedExpense.setAmount(200);
        updatedExpense.setDescription("Business trip");

        when(expenseRepository.findById(1L)).thenReturn(Optional.of(expense));
        when(expenseRepository.save(any(Expense.class))).thenReturn(updatedExpense);

        Expense result = expenseService.updateExpense(updatedExpense);

        assertEquals("Travel", result.getCategory());
        assertEquals(200, result.getAmount());
        assertEquals("Business trip", result.getDescription());
    }

    @Test
    void testUpdateExpense_NotFound() {
        when(expenseRepository.findById(1L)).thenReturn(Optional.empty());

        ExpenseCustomException exception = assertThrows(ExpenseCustomException.class, () -> {
            expenseService.updateExpense(expense);
        });

        assertEquals("Expense does not exist with Id: 1", exception.getMessage());
    }

    @Test
    void testDeleteExpense_NotFound() {
        when(expenseRepository.existsById(1L)).thenReturn(false);

        ExpenseCustomException exception = assertThrows(ExpenseCustomException.class, () -> {
            expenseService.deleteExpense(1L);
        });

        assertEquals("expense does not exist with Id: 1", exception.getMessage());
    }

    @Test
    void testGetCategoryExpenseTotalAmount_Success() {
        when(expenseRepository.findSumExpenseByUserCategory(1L, "food")).thenReturn(Optional.of(300.0));

        double result = expenseService.getCategoryExpenseTotalAmount(1L, "Food");

        assertEquals(300.0, result);
    }

    @Test
    void testGetCategoryExpenseTotalAmount_NotFound() {
        when(expenseRepository.findSumExpenseByUserCategory(1L, "food")).thenReturn(Optional.empty());

        ExpenseCustomException exception = assertThrows(ExpenseCustomException.class, () -> {
            expenseService.getCategoryExpenseTotalAmount(1L, "Food");
        });

        assertEquals("Expense does not exist!!!", exception.getMessage());
    }
}
