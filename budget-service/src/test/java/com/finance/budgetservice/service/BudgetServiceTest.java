package com.finance.budgetservice.service;

import com.finance.budgetservice.exception.BudgetCustomException;
import com.finance.budgetservice.model.Budget;
import com.finance.budgetservice.repository.BudgetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BudgetServiceTest {

    @Mock
    private BudgetRepository budgetRepository;

    @InjectMocks
    private BudgetService budgetService;

    private Budget budget;

    @BeforeEach
    void setUp() {
        budget = new Budget();
        budget.setId(1L);
        budget.setUserId(1L);
        budget.setCategory("Food");
        budget.setAmount(500);
    }

    @Test
    void testGetUserSpecificBudget() {
        when(budgetRepository.findByUserId(1L)).thenReturn(Optional.of(List.of(budget)));

        Optional<List<Budget>> result = budgetService.getUserSpecificBudget(1L);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().size());

    }

    @Test
    void testGetBudgetFromId() {
        when(budgetRepository.findById(1L)).thenReturn(Optional.of(budget));

        Optional<Budget> result = budgetService.getBudgetFromId(1L);

        assertTrue(result.isPresent());
        assertEquals("Food", result.get().getCategory());
    }

    @Test
    void testCreateBudgetAlreadyExists() {
        when(budgetRepository.findBudgetByUserCategory(1L, "Food")).thenReturn(Optional.of(budget));

        BudgetCustomException exception = assertThrows(BudgetCustomException.class, () -> budgetService.createBudget(budget));

        assertEquals("Budget already exists!!!!", exception.getMessage());

    }

    @Test
    void testUpdateBudget() {
        when(budgetRepository.findById(1L)).thenReturn(Optional.of(budget));

        Budget updatedBudget = new Budget();
        updatedBudget.setId(1L);
        updatedBudget.setCategory("Travel");
        updatedBudget.setAmount(1000);

        budgetService.updateBudget(updatedBudget);

        assertEquals("Travel", budget.getCategory());
        assertEquals(1000, budget.getAmount());
    }

    @Test
    void testUpdateBudgetNotFound() {
        when(budgetRepository.findById(1L)).thenReturn(Optional.empty());

        Budget updatedBudget = new Budget();
        updatedBudget.setId(1L);

        BudgetCustomException exception = assertThrows(BudgetCustomException.class, () -> budgetService.updateBudget(updatedBudget));

        assertEquals("Budget does not exist with Id: 1", exception.getMessage());
    }

    @Test
    void testDeleteBudgetNotFound() {
        when(budgetRepository.findById(1L)).thenReturn(Optional.empty());

        BudgetCustomException exception = assertThrows(BudgetCustomException.class, () -> budgetService.deleteBudget(1L));

        assertEquals("Budget does not exist with Id: 1", exception.getMessage());
    }

    @Test
    void testGetUserCategoryBudget() {
        when(budgetRepository.findBudgetByUserCategory(1L, "Food")).thenReturn(Optional.of(budget));

        Budget result = budgetService.getUserCategoryBudget(1L, "Food");

        assertNotNull(result);
        assertEquals("Food", result.getCategory());
    }

    @Test
    void testGetUserCategoryBudgetNotFound() {
        when(budgetRepository.findBudgetByUserCategory(1L, "Food")).thenReturn(Optional.empty());

        BudgetCustomException exception = assertThrows(BudgetCustomException.class, () -> budgetService.getUserCategoryBudget(1L, "Food"));

        assertEquals("Budget does not exist with Id: 1", exception.getMessage());
    }
}
