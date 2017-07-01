package com.basicBudgetBuilder.service;

import com.basicBudgetBuilder.domain.User;
import com.basicBudgetBuilder.exceptions.BasicBudgetBuilderException;
import com.basicBudgetBuilder.representation.AutoDebitRep;
import com.basicBudgetBuilder.representation.BudgetRep;
import com.basicBudgetBuilder.representation.DebitRep;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Service for AutoDebit related requests called by the controller
 * Created by Hanzi Jing on 10/04/2017.
 */
@Service
public interface AutoDebitService {
    List<AutoDebitRep> getAllForCategories(User user, List<BudgetRep>budgetReps)throws BasicBudgetBuilderException;
    AutoDebitRep create(AutoDebitRep autoDebitRep, User user)throws BasicBudgetBuilderException;
    AutoDebitRep edit(AutoDebitRep autoDebitRep, User user)throws BasicBudgetBuilderException;
    Boolean delete(long id)throws BasicBudgetBuilderException;
    Map<Long, BigDecimal>getCostsForBudgets(User user, List<BudgetRep> budgetReps) throws BasicBudgetBuilderException;
}