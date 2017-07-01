package com.basicBudgetBuilder.representation;

import com.basicBudgetBuilder.domain.Interval;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Entity class for representing budget fulltext search results
 * Created by Hanzi Jing on 8/04/2017.
 */
@Data
public class BudgetFullTextSearchRep implements FullTextSearchEntity{
    private final FullTextEntityType type = FullTextEntityType.BUDGET;
    private BudgetRep budgetRep;
    private double score;
    private String description;

    public BudgetFullTextSearchRep(){}

    /** constructor for sending information to client side in a format that is easy to process */
    public BudgetFullTextSearchRep(double score,
                                   long id,
                                   long userId,
                                   long categoryId,
                                   String categoryName,
                                   String categoryColour,
                                   String description,
                                   BigDecimal amount,
                                   Interval budgetInterval,
                                   String effectiveDate){
        this.score = score;
        this.description =  "\t" + categoryName + " (" + description + ") " + "; Amount: "
                + amount.toString() + "/" + budgetInterval.name() + "; Effective Date: " + effectiveDate;
        this.budgetRep = new BudgetRep(
                id,
                userId,
                categoryId,
                categoryName,
                categoryColour,
                description,
                amount,
                budgetInterval,
                effectiveDate
        );
    }
}
