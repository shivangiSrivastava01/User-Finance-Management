package com.finance.budgetservice.exception;

public class BudgetCustomException extends RuntimeException{

    public BudgetCustomException(String message){
        super(message);
    }
}
