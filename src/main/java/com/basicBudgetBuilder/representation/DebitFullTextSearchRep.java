package com.basicBudgetBuilder.representation;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Entity class for representing debit fulltext search results
 * Created by Hanzi Jing on 8/04/2017.
 */
@Data
public class DebitFullTextSearchRep implements FullTextSearchEntity{
    private final FullTextEntityType type = FullTextEntityType.DEBIT;
    private DebitRep debitRep;
    private double score;
    private String description;

    public DebitFullTextSearchRep(){}
    /** constructor for sending information to client side in a format that is easy to process */
    public DebitFullTextSearchRep(double score,
                                  long id,
                                  long userId,
                                  long categoryId,
                                  String categoryName,
                                  String categoryColour,
                                  String description,
                                  BigDecimal amount,
                                  String date){
        this.score = score;
        this.description =  "\t" + categoryName + " (" + description + ") " + "; Amount: " + amount.toString() + "; Date: " + date;
        this.debitRep = new DebitRep(
                id,
                userId,
                categoryId,
                categoryName,
                categoryColour,
                description,
                amount,
                date
        );
    }
}
