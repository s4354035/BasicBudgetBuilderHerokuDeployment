package com.basicBudgetBuilder.domain;

import lombok.Data;


import javax.persistence.*;

/**
 * Entity class and Table for storing Categories
 * Created by Hanzi Jing on 6/04/2017.
 */
@Data
@Entity
@Table(name = "category",
        uniqueConstraints={
            @UniqueConstraint(columnNames = {"user_id", "name"}),
                @UniqueConstraint(columnNames = {"user_id", "colour"})
        })
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false )
    private String name;

    @Column(nullable = false)
    private String colour;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Category(){}
    /** constructor with ID for sending information to client or changing existing entries */
    public Category(long id, String name, String colour){
        this.id = id;
        this.name = name;
        this.colour = colour;
    }
    /** constructor without ID for creating new entries */
    public Category(String name, String colour, User user){
        this.name = name;
        this.colour = colour;
        this.user = user;
    }
}
