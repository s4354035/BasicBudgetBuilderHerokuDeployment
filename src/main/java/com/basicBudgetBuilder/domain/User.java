package com.basicBudgetBuilder.domain;

import com.basicBudgetBuilder.representation.UserRep;
import lombok.Data;
import javax.persistence.*;

/**
 * Entity class and Table for storing Users
 * Created by Hanzi Jing on 3/04/2017.
 */
@Data
@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Role role;

    public User(){
    }
    /** constructor without ID for creating new entries */
    public User(String email, String password, Role role, String name){

        this.email = email;
        this.password = password;
        this.role = role;
        this.name = name;
    }
    /** constructor directly using a user representation */
    public User(UserRep userRep){
        if(userRep != null){
            this.email = userRep.getEmail();
            this.password = userRep.getPassword();
            this.role = Role.USER;
            this.name = userRep.getName();
        }
    }
}