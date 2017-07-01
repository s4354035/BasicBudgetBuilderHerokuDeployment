package com.basicBudgetBuilder.repository;

import com.basicBudgetBuilder.domain.AutoDebit;
import com.basicBudgetBuilder.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Repository for Standard autodebit related query entries
 * Created by Hanzi Jing on 9/04/2017.
 */
@Transactional(readOnly = true)
public interface AutoDebitRepository extends JpaRepository<AutoDebit, Long> {
    List<AutoDebit>findByUser(User user);
    AutoDebit findById(long id);
}

