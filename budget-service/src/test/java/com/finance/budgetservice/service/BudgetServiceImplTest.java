package com.finance.budgetservice.service;

import com.finance.budgetservice.exception.BudgetCustomException;
import com.finance.budgetservice.model.Budget;
import com.finance.budgetservice.model.BudgetRequest;
import com.finance.budgetservice.model.BudgetResponse;
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
class BudgetServiceImplTest {

    @Mock
    private BudgetRepository budgetRepository;

    @InjectMocks
    private BudgetServiceImpl budgetServiceImpl;

    private Budget budget;

    @BeforeEach
    void setUp() {

        budget = new Budget();
        budget.setId(1L);
        budget.setUserId(1L);
        budget.setCategory("Travel");
        budget.setAmount(500);

    }

    @Test
    void testGetUserSpecificBudget() {
        when(budgetRepository.findByUserId(1L)).thenReturn(Optional.of(List.of(budget)));

        Optional<List<BudgetResponse>> result = budgetServiceImpl.getUserSpecificBudget(1L);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().size());

    }


    @Test
    void testUpdateBudgetNotFound() {
        when(budgetRepository.findById(1L)).thenReturn(Optional.empty());

        BudgetRequest updatedBudget = new BudgetRequest();
        updatedBudget.setId(1L);

        BudgetCustomException exception = assertThrows(BudgetCustomException.class, () -> budgetServiceImpl.updateBudget(updatedBudget));

        assertEquals("Budget does not exist with Id: 1", exception.getMessage());
    }

    @Test
    void testDeleteBudgetNotFound() {
        when(budgetRepository.findById(1L)).thenReturn(Optional.empty());

        BudgetCustomException exception = assertThrows(BudgetCustomException.class, () -> budgetServiceImpl.deleteBudget(1L));

        assertEquals("Budget does not exist with Id: 1", exception.getMessage());
    }

    @Test
    void testGetUserCategoryBudgetNotFound() {
        when(budgetRepository.findBudgetByUserCategory(1L, "Food")).thenReturn(Optional.empty());

        BudgetCustomException exception = assertThrows(BudgetCustomException.class, () -> budgetServiceImpl.getUserCategoryBudget(1L, "Food"));

        assertEquals("Budget does not exist with Id: 1", exception.getMessage());
    }
}
