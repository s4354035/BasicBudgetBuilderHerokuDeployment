package com.basicBudgetBuilder.representation;

import com.basicBudgetBuilder.domain.Interval;
import lombok.Data;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Entity class for representing autodebit entries
 * Created by Hanzi Jing on 8/04/2017.
 */
@Data
public class AutoDebitRep {
    private long id;
    private long userId;
    private long categoryId;
    private String categoryName;
    private String categoryColour;
    private String description;
    private BigDecimal amount;
    private Interval debitInterval;

    public AutoDebitRep(){}
    /** constructor without ID for creating new entries */
    public AutoDebitRep(String categoryName,
                        String categoryColour,
                        String description,
                        BigDecimal amount,
                        Interval debitInterval){
        this.categoryName = categoryName;
        this.categoryColour = categoryColour;
        this.description = description;
        this.amount =  amount != null ? amount.setScale(2, RoundingMode.HALF_UP) : null;
        this.debitInterval = debitInterval;
    }
    /** constructor with ID for sending information to client or changing existing entries */
    public AutoDebitRep(long id,
                        long userId,
                        long categoryId,
                        String categoryName,
                        String categoryColour,
                        String description,
                        BigDecimal amount,
                        Interval debitInterval){
        this.id = id;
        this.userId = userId;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.categoryColour = categoryColour;
        this.description = description;
        this.amount = amount != null ? amount.setScale(2, RoundingMode.HALF_UP) :null;
        this.debitInterval = debitInterval;
    }
    public void setAmount(BigDecimal amount){
        this.amount = amount != null ? amount.setScale(2, RoundingMode.HALF_UP) :null;
    }
}