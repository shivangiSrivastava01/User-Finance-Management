package com.finance.expenseservice.client;

import com.finance.expenseservice.dto.BudgetDTO;
import com.finance.expenseservice.exception.ExpenseCustomException;
import com.finance.expenseservice.model.Expense;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BudgetClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private BudgetClient budgetClient;

    private Expense expense;
    private BudgetDTO budgetDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        expense = new Expense();
        expense.setUserId(1L);
        expense.setCategory("Food");
        budgetDTO = new BudgetDTO();
        budgetDTO.setUserId(1L);
        budgetDTO.setCategory("Food");
        budgetDTO.setAmount(100.0);

        budgetClient = new BudgetClient(this.restTemplate);
    }

    @Test
    void testGetBudget_Success() {
        String expenseServiceUrl = UriComponentsBuilder.fromHttpUrl("http://localhost:8080/financeManagement/userCategoryBudget")
                .queryParam("userId", expense.getUserId())
                .queryParam("category", expense.getCategory())
                .toUriString();

        when(restTemplate.getForObject(expenseServiceUrl, BudgetDTO.class)).thenReturn(budgetDTO);

        BudgetDTO result = budgetClient.getBudget(expense);

        assertNotNull(result);
        assertEquals(budgetDTO, result);
        verify(restTemplate, times(1)).getForObject(expenseServiceUrl, BudgetDTO.class);
    }

    @Test
    void testGetBudget_Exception() {
        String expenseServiceUrl = UriComponentsBuilder.fromHttpUrl("http://localhost:8080/financeManagement/userCategoryBudget")
                .queryParam("userId", expense.getUserId())
                .queryParam("category", expense.getCategory())
                .toUriString();

        when(restTemplate.getForObject(expenseServiceUrl, BudgetDTO.class)).thenThrow(new RuntimeException("Service is down"));

        ExpenseCustomException exception = assertThrows(ExpenseCustomException.class, () -> budgetClient.getBudget(expense));

        assertEquals("Budget for the requested User Id and category does not exists", exception.getMessage());
        verify(restTemplate, times(1)).getForObject(expenseServiceUrl, BudgetDTO.class);
    }
}
