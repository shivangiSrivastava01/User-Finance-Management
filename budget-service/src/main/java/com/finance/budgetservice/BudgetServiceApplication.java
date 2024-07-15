package com.finance.budgetservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class BudgetServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BudgetServiceApplication.class, args);
	}

}
