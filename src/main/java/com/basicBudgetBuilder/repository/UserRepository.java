package com.basicBudgetBuilder.repository;

import com.basicBudgetBuilder.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for user related query entries
 * Created by Hanzi Jing on 3/04/2017.
 */
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    User findById(long id);
}