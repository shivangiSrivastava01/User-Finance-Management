package com.finance.expenseservice.client;

import com.finance.expenseservice.dto.BudgetDTO;
import com.finance.expenseservice.exception.ExpenseCustomException;
import com.finance.expenseservice.model.Expense;
import com.finance.expenseservice.model.ExpenseResponse;
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

    private ExpenseResponse expenseResponse;
    private BudgetDTO budgetDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        expenseResponse = new ExpenseResponse();
        expenseResponse.setUserId(1L);
        expenseResponse.setCategory("Food");
        budgetDTO = new BudgetDTO();
        budgetDTO.setUserId(1L);
        budgetDTO.setCategory("Food");
        budgetDTO.setAmount(100.0);

        budgetClient = new BudgetClient(this.restTemplate);
    }

    @Test
    void testGetBudget_Success() {
        String expenseServiceUrl = UriComponentsBuilder.fromHttpUrl("http://localhost:8080/financeManagement/userCategoryBudget")
                .queryParam("userId", expenseResponse.getUserId())
                .queryParam("category", expenseResponse.getCategory())
                .toUriString();

        when(restTemplate.getForObject(expenseServiceUrl, BudgetDTO.class)).thenReturn(budgetDTO);

        BudgetDTO result = budgetClient.getBudget(expenseResponse);

        assertNotNull(result);
        assertEquals(budgetDTO, result);
        verify(restTemplate, times(1)).getForObject(expenseServiceUrl, BudgetDTO.class);
    }

    @Test
    void testGetBudget_Exception() {
        String expenseServiceUrl = UriComponentsBuilder.fromHttpUrl("http://localhost:8080/financeManagement/userCategoryBudget")
                .queryParam("userId", expenseResponse.getUserId())
                .queryParam("category", expenseResponse.getCategory())
                .toUriString();

        when(restTemplate.getForObject(expenseServiceUrl, BudgetDTO.class)).thenThrow(new RuntimeException("Service is down"));

        ExpenseCustomException exception = assertThrows(ExpenseCustomException.class, () -> budgetClient.getBudget(expenseResponse));

        assertEquals("Budget for the requested User Id and category does not exists", exception.getMessage());
        verify(restTemplate, times(1)).getForObject(expenseServiceUrl, BudgetDTO.class);
    }
}
