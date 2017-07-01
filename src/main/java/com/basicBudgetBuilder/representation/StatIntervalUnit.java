package com.basicBudgetBuilder.representation;

import lombok.Data;

import java.util.Map;

/**
 * Created by Hanzi Jing on 13/05/2017.
 * For future use
 */
@Data
public class StatIntervalUnit{
    private String categoryName;
    private String categoryColour;
    Map<String, BudgetDebitRep> intervalBudgetDebit;

    public StatIntervalUnit(CategoryRep categoryRep, Map<String, BudgetDebitRep> intervalMap){
        this.categoryColour = categoryRep.getColour();
        this.categoryName = categoryRep.getName();
        this.intervalBudgetDebit = intervalMap;
    }
}