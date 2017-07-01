package com.basicBudgetBuilder.repository;

import com.basicBudgetBuilder.domain.Category;
import com.basicBudgetBuilder.domain.Debit;
import com.basicBudgetBuilder.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repository for Standard debit related query entries
 * Created by Hanzi Jing on 9/04/2017.
 */
public interface DebitRepository extends JpaRepository<Debit, Long> {
    List<Debit>findByUser(User user);
    List<Debit>findByUserAndCategoryIn(User user, List<Category> categories);
    Debit findById(long id);
}

