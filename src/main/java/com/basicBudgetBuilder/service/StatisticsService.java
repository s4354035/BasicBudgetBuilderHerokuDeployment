package com.basicBudgetBuilder.service;

import com.basicBudgetBuilder.domain.User;
import com.basicBudgetBuilder.exceptions.BasicBudgetBuilderException;
import com.basicBudgetBuilder.representation.BudgetRep;
import com.basicBudgetBuilder.representation.StatRep;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Service for statistics related requests called by the controller
 * Created by Hanzi Jing on 10/04/2017.
 */
@Service
public interface StatisticsService {
    StatRep getStats(User user, List<BudgetRep>budgetReps)throws BasicBudgetBuilderException;
}
