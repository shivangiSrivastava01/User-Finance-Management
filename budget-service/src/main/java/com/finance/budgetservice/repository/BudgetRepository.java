package com.finance.budgetservice.repository;

import com.finance.budgetservice.model.Budget;
import com.finance.budgetservice.model.BudgetResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget,Long> {

    @Query(value = "select * from budgets where user_id= :userId", nativeQuery = true)
    Optional<List<Budget>> findByUserId(@Param("userId") Long userId);

    @Query(value = "select * from budgets where user_id= :userId and lower(category)=:category", nativeQuery = true)
    Optional<Budget> findBudgetByUserCategory(@Param("userId") Long userId, @Param("category") String category);



}
