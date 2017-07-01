package com.basicBudgetBuilder.domain;

import lombok.Data;
import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Entity class and Table for storing autoDebits
 * Created by Hanzi Jing on 6/04/2017.
 */
// Start and end date was not implemented due to time constraints
// and complications to calculation algorithm
@Data
@Entity
@Table(name = "autodebit")
public class AutoDebit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column
    private String description;

    @Column(nullable = false)
    private Interval debitInterval;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Column
    private String categoryName;

    public AutoDebit(){}
    /** constructor without ID for creating new entries */
    public AutoDebit(String description,
                     BigDecimal amount,
                     Interval debitInterval,
                     User user,
                     Category category){
        this.description = description;
        this.amount = amount != null ? amount.setScale(2, RoundingMode.HALF_UP) : null;
        this.debitInterval = debitInterval;
        this.user = user;
        this.category = category;
        if(category!= null)
            this.categoryName = category.getName();
    }
    /** constructor with ID for sending information to client or changing existing entries */
    public AutoDebit(long id,
                     String description,
                     BigDecimal amount,
                     Interval debitInterval,
                     Category category){
        this.id = id;
        this.description = description;
        this.amount =  amount != null ?  amount.setScale(2, RoundingMode.HALF_UP) : null;
        this.debitInterval = debitInterval;
        this.user = user;
        this.category = category;
        if(category!= null)
            this.categoryName = category.getName();
    }
    public void setCategory(Category category){
        this.category = category;
        if(category!= null)
            this.categoryName = category.getName();
    }
    public void setAmount(BigDecimal amount){
        this.amount = amount != null ? amount.setScale(2, RoundingMode.HALF_UP) : null;
    }
}
