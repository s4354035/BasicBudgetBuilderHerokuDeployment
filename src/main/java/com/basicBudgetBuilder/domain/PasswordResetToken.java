package com.basicBudgetBuilder.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;

/**
 * Entity class and Table for storing Password reset tokens
 * Created by Hanzi Jing on 4/27/2017.
 */
@Data
@Entity
public class PasswordResetToken {

    private static final int EXPIRATION = 60 * 24;

    @Id
    private String token;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    @Column(nullable = false)
    private Date expiryDate;

    public PasswordResetToken(){}

    /** Reset password token constructor */
    public PasswordResetToken(String token, User user){
        this.token = token;
        this.user = user;
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, EXPIRATION);
        this.expiryDate = cal.getTime();
    }
}
