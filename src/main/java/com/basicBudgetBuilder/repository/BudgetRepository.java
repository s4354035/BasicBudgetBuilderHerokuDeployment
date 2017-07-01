package com.basicBudgetBuilder.repository;

import com.basicBudgetBuilder.domain.Budget;
import com.basicBudgetBuilder.domain.Category;
import com.basicBudgetBuilder.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

/**
 * Repository for Standard budget related query entries
 * Created by Hanzi Jing on 9/04/2017.
 */
public interface BudgetRepository extends JpaRepository<Budget, Long> {
    List<Budget> findByCategoryAndEffectiveDateAndUser(Category category, Date effectiveDate, User user);
    Budget findById(long id);
    List<Budget> findByCategoryAndUser(Category category, User user);
}

