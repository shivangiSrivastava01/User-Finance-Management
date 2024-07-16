package com.finance.notificationservice.controller;

import com.finance.notificationservice.service.NotificationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/financeManagement")
@Tag(name = "Notification Service", description = "Operations pertaining to notification in Finance Management System")
public class NotificationController {

    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService){
        this.notificationService = notificationService;
    }

    @GetMapping("/notifyUser")
    public String getNotifications(String budgetCategory,
                                 double budgetAmount,
                                 String expenseDescription,
                                 double expenseAmount,
                                 String userEmail){

        log.info("Expense({}) exceeds Budget({}) for {} for {}. Description of Latest Expense - {}",
                expenseAmount, budgetAmount, budgetCategory, userEmail, expenseDescription);

        String text = "Dear User,\n\n" +
                "We want to inform you that your total expense of **"+expenseAmount+"** has exceeded your budget of **"+budgetAmount+"** for **"+budgetCategory+"**.\n\n" +
                "Description of Last Expense: "+expenseDescription+"\n\n" +
                "Please review your expenses at your earliest convenience.\n\n" +
                "Best regards!";

        notificationService.sendSimpleMessage(
                "systemtest.email01@gmail.com",
                "Budget Exceeded!",
                "mailtrap@demomailtrap.com",
                text);

        return text;
    }
}
