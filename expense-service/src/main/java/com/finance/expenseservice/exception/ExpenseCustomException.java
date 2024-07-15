package com.finance.expenseservice.exception;

public class ExpenseCustomException extends RuntimeException{

    public ExpenseCustomException(String message){
        super(message);
    }
}
