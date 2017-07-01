package com.basicBudgetBuilder.representation;

import lombok.Data;
import java.math.BigDecimal;

/**
 * Created by Hanzi Jing on 13/05/2017.
 * For future use
 */
@Data
public class StatUnit{
    private String categoryName;
    private String categoryColour;
    private BigDecimal budget;
    private BigDecimal debit;
    public StatUnit(CategoryRep categoryRep, BudgetDebitRep budgetDebitRep){
        this.categoryColour = categoryRep.getColour();
        this.categoryName = categoryRep.getName();
        this.budget = budgetDebitRep.getBudget();
        this.debit = budgetDebitRep.getDebit();
    }
}