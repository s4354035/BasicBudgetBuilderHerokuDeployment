package com.basicBudgetBuilder.domain;

import lombok.Data;
import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

/**
 * Entity class and Table for storing Debit entries
 * Created by Hanzi Jing on 6/04/2017.
 */
@Data
@Entity
@Table(name = "Debit")
public class Debit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String description;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private Date date;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Column
    private String categoryName;

    @ManyToOne
    @JoinColumn(name = "autodebit_id")
    private AutoDebit autodebit;

    public Debit(){}
    /** constructor without ID for creating new entries */
    public Debit(String description,
                 BigDecimal amount,
                 Date date,
                 User user,
                 Category category,
                 AutoDebit autodebit){
        this.description = description;
        this.amount = amount;
        this.date = date;
        this.user = user;
        this.category = category;
        this.autodebit = autodebit;
        if(category!= null){
            this.categoryName = category.getName();
        }
    }
    /**  constructor with ID for generating entries via autoDebit
     * (unimplemented due to lack of time and large database usage)*/
    public Debit(long id,
                 String description,
                 BigDecimal amount,
                 Date date,
                 Category category,
                 AutoDebit autodebit){
        this.id = id;
        this.description = description;
        this.amount =  amount != null ? amount.setScale(2, RoundingMode.HALF_UP) : null;
        this.date = date;
        this.category = category;
        this.autodebit = autodebit;
    }
    /** constructor with ID for sending information to client or changing existing entries */
    public Debit(long id,
                 String description,
                 BigDecimal amount,
                 Date date,
                 Category category){
        this.id = id;
        this.description = description;
        this.amount =  amount != null ? amount.setScale(2, RoundingMode.HALF_UP) : null;
        this.date = date;
        this.category = category;
        if(category!= null){
            this.categoryName = category.getName();
        }
    }
    public void setCategory(Category category){
        this.category = category;
        if(category!= null)
            this.categoryName = category.getName();
    }
    public void setAmount(BigDecimal amount){
        this.amount =  amount != null ? amount.setScale(2, RoundingMode.HALF_UP) : null;
    }
}
