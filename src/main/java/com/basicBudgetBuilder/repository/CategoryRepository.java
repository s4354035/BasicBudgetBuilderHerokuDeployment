package com.basicBudgetBuilder.repository;

import com.basicBudgetBuilder.domain.Category;
import com.basicBudgetBuilder.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository for Standard Category related query entries
 * Created by Hanzi Jing on 9/04/2017.
 */
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByNameAndUser(String name, User user);
    Category findByColourAndUser(String colour, User user);
    List<Category> findByUserId(long userId);
}

