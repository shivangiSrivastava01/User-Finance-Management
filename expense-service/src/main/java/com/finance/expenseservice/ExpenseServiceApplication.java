package com.finance.expenseservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ExpenseServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExpenseServiceApplication.class, args);
	}

}
