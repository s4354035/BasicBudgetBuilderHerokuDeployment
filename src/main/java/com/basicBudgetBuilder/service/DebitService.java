package com.basicBudgetBuilder.service;

import com.basicBudgetBuilder.domain.Budget;
import com.basicBudgetBuilder.domain.Category;
import com.basicBudgetBuilder.domain.Interval;
import com.basicBudgetBuilder.domain.User;
import com.basicBudgetBuilder.exceptions.BasicBudgetBuilderException;
import com.basicBudgetBuilder.representation.BudgetRep;
import com.basicBudgetBuilder.representation.DebitRep;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Service for debit related requests called by the controller
 * Created by Hanzi Jing on 10/04/2017.
 */
@Service
public interface DebitService {
    List<DebitRep> getByCategories(User user, List<BudgetRep> budgetReps)throws BasicBudgetBuilderException;
    BigDecimal getCurrentSpending(User user, Budget budget)throws BasicBudgetBuilderException;
    DebitRep create(DebitRep debitRep, User user)throws BasicBudgetBuilderException;
    DebitRep edit(DebitRep debitRep, User user)throws BasicBudgetBuilderException;
    Boolean delete(long id)throws BasicBudgetBuilderException;
}
