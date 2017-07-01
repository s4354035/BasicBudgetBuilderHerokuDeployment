package com.basicBudgetBuilder.service;

import com.basicBudgetBuilder.domain.User;
import com.basicBudgetBuilder.exceptions.BasicBudgetBuilderException;
import com.basicBudgetBuilder.representation.UserRep;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.Principal;

/**
 * Service for user related requests called by the controller
 * Created by Hanzi Jing on 4/04/2017.
 */
public interface UserService {
    void save(User user);
    User save(UserRep userRep)throws BasicBudgetBuilderException;
    User findByEmail(String username);
    User getUserFromSession(HttpSession session);
    String resetPassword(String email, HttpServletRequest request)throws BasicBudgetBuilderException;
    void createPasswordTokenForUser(User user, String token);
    String validatePasswordResetToken(long id, String token);
    String updatePassword(Principal principal, String password, String confrirmPassword)throws BasicBudgetBuilderException;
}
