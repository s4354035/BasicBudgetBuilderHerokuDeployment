package com.basicBudgetBuilder.representation;

import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Entity class for representing debit entries
 * Created by Hanzi Jing on 8/04/2017.
 */
@Data
public class DebitRep {
    private long id;
    private long userId;
    private long categoryId;
    private String categoryName;
    private String categoryColour;
    private String description;
    private BigDecimal amount;
    private String date;
    private long autoDebitId;
    private String attachments;

    public DebitRep(){
    }
    /** constructor without ID for creating new entries */
    public DebitRep(String categoryName,
                    String categoryColour,
                    String description,
                    BigDecimal amount,
                    String date){
        this.categoryName = categoryName;
        this.categoryColour = categoryColour;
        this.description = description;
        this.amount = amount != null ? amount.setScale(2, RoundingMode.HALF_UP) : null;
        this.date = date;
    }
    /** constructor with ID for sending information to client or changing existing entries */
    public DebitRep(long id,
                    long userId,
                    long categoryId,
                    String categoryName,
                    String categoryColour,
                    String description,
                    BigDecimal amount,
                    String date){
        this.id = id;
        this.userId = userId;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.categoryColour = categoryColour;
        this.description = description;
        this.amount = amount != null ? amount.setScale(2, RoundingMode.HALF_UP) : null;
        this.date = date;
    }
    public void setAmount(BigDecimal amount){
        this.amount =  amount != null ? amount.setScale(2, RoundingMode.HALF_UP) : null;
    }
}
