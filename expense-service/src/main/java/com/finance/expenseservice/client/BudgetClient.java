package com.finance.expenseservice.client;

import com.finance.expenseservice.dto.BudgetDTO;
import com.finance.expenseservice.exception.ExpenseCustomException;
import com.finance.expenseservice.model.Expense;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
public class BudgetClient {
    private final RestTemplate restTemplate;

    @Autowired
    public BudgetClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public BudgetDTO getBudget(Expense expense) {
        try{
            log.info("call Budget service to fetch the budgetData::");
            String expenseServiceUrl = UriComponentsBuilder.fromHttpUrl("http://localhost:8080/financeManagement/userCategoryBudget")
                    .queryParam("userId", expense.getUserId())
                    .queryParam("category", expense.getCategory())
                    .toUriString();
            return restTemplate.getForObject(expenseServiceUrl, BudgetDTO.class);

        }catch (Exception e){
            throw new ExpenseCustomException("Budget for the requested User Id and category does not exists");
        }

    }
}
