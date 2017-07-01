package com.basicBudgetBuilder.service;

import com.basicBudgetBuilder.domain.User;
import com.basicBudgetBuilder.exceptions.BasicBudgetBuilderException;
import com.basicBudgetBuilder.representation.BudgetRep;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Service for budget related requests called by the controller
 * Created by Hanzi Jing on 9/04/2017.
 */
@Service
public interface BudgetService {
    List<BudgetRep> getAll(User user)throws BasicBudgetBuilderException;
    BudgetRep create(BudgetRep budgetRep, User user)throws BasicBudgetBuilderException;
    BudgetRep edit(BudgetRep budgetRep, User user)throws BasicBudgetBuilderException;
    Boolean delete(long id)throws BasicBudgetBuilderException;
}
