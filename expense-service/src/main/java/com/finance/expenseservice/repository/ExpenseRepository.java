package com.finance.expenseservice.repository;
import com.finance.expenseservice.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense,Long> {

    @Query(value = "select * from expenses where user_id= :userId", nativeQuery = true)
    Optional<List<Expense>> findByUserId(@Param("userId") Long userId);

    @Query(value = "select * from expenses where user_id= :userId and lower(category)=:category", nativeQuery = true)
    Optional<List<Expense>> findExpenseByUserCategory(@Param("userId") Long userId, @Param("category") String category);

    @Query(value = "select sum(amount) as expense from expenses where user_id= :userId and lower(category)=:category group by 1, 2", nativeQuery = true)
    Optional<Double> findSumExpenseByUserCategory(@Param("userId") Long userId, @Param("category") String category);

}
