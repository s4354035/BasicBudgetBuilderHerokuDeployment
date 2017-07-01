package com.basicBudgetBuilder.service;

import com.basicBudgetBuilder.domain.Interval;
import com.basicBudgetBuilder.domain.PasswordResetToken;
import com.basicBudgetBuilder.domain.User;
import com.basicBudgetBuilder.exceptions.BasicBudgetBuilderException;
import com.basicBudgetBuilder.repository.PasswordTokenRepository;
import com.basicBudgetBuilder.repository.UserRepository;
import com.basicBudgetBuilder.representation.BudgetRep;
import com.basicBudgetBuilder.representation.UserRep;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Implementation of the User Service
 * Created by Hanzi Jing on 4/04/2017.
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordTokenRepository passwordTokenRepository;
    @Autowired
    private Environment environment;
    @Autowired
    private MailSender mailSender;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private BudgetService budgetService;

    /** saves the user into the database */
    @Override
    public void save(User user){
        try {
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            userRepository.save(user);
            if (user.getId() > 0) {
                createUncategorizedBudget(user);
            }
        }catch (Exception exp){
            exp.printStackTrace();
        }
    }

    /** saves the user into the database */
    public User save(UserRep userRep) throws BasicBudgetBuilderException {
        Map<String, String> errors = Maps.newHashMap();
        try {
            validate(userRep);
            User user = new User(userRep);
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            user = userRepository.save(user);
            if(user.getId() > 0){
                createUncategorizedBudget(user);
            }
            return user;
        } catch (DataAccessException e) {
            errors.put("general", e.getMessage());
            throw new BasicBudgetBuilderException(errors);
        }

    }
    /** gets the user from database by e-mail */
    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    /**
     * Checks username in session
     * session will not contain user information after session timeout
     * (last line of security)
     *
     * @param session required
     * @return session user
     */
    @Override
    public User getUserFromSession(HttpSession session) {
        if (session == null) return null;
        Object context = session.getAttribute("SPRING_SECURITY_CONTEXT");
        if (context != null && context instanceof SecurityContextImpl) {
            Authentication authentication = ((SecurityContextImpl) context).getAuthentication();
            if (authentication != null) {
                Object principal = authentication.getPrincipal();
                if (principal != null && principal instanceof org.springframework.security.core.userdetails.User) {
                    return this.findByEmail(((org.springframework.security.core.userdetails.User) principal).getUsername());
                }
            }
        }
        return null;
    }

    /**
     * Validates whether the input is correct
     *
     * @param userRep required
     * @throws BasicBudgetBuilderException Exception Description
     */
    private void validate(UserRep userRep) throws BasicBudgetBuilderException {
        Map<String, String> errors = Maps.newHashMap();
        if (userRep == null) {
            errors.put("general", "No Input Data");
            throw new BasicBudgetBuilderException(errors);
        }
        if (userRep.getEmail() == null || userRep.getEmail().trim().isEmpty()) {
            errors.put("email", "Required");
        } else if (!userRep.getEmail().trim().matches("^\\S+\\@\\S+$")) {
            errors.put("email", "Invalid Format");
        }
        if (userRep.getPassword() == null || userRep.getPassword().isEmpty()) {
            errors.put("password", "Required");
        }
        if (userRep.getConfirmPassword() == null || userRep.getConfirmPassword().isEmpty()) {
            errors.put("confirmPassword", "Required");
        }
        if (userRep.getPassword() != null && userRep.getConfirmPassword() != null
                && !userRep.getPassword().equals(userRep.getConfirmPassword())) {
            errors.put("confirmPassword", "Password Does not match!");
        }
        if (userRep.getName() == null || userRep.getName().trim().isEmpty()) {
            errors.put("name", "Required");
        }
        if (userRep.getEmail() != null && userRepository.findByEmail(userRep.getEmail().trim()) != null) {
            errors.put("email", "Has already been used");
        }
        if (!errors.isEmpty()) {
            throw new BasicBudgetBuilderException(errors);
        }
    }

    /**
     * Sends a reset password token to the user e-mail and return a statement
     *
     * @param email   required
     * @param request required
     * @return success message string
     * @throws BasicBudgetBuilderException Exception Description
     */
    public String resetPassword(String email, HttpServletRequest request) throws BasicBudgetBuilderException {
        Map<String, String> errors = Maps.newHashMap();
        User user = findByEmail(email);
        if (user == null) {
            errors.put("email", "Invalid E-mail Address");
            throw new BasicBudgetBuilderException(errors);
        }
        String token = UUID.randomUUID().toString();
        createPasswordTokenForUser(user, token);
        mailSender.send(constructResetTokenEmail(request.getRequestURL().toString(), request.getLocale(), token, user));
        return "Reset Password Email has been sent";
    }

    /**
     * reset password of the user
     *
     * @param principal       required
     * @param password        required
     * @param confirmPassword required
     * @return success message string
     * @throws BasicBudgetBuilderException Exception Description
     */
    public String updatePassword(Principal principal, String password, String confirmPassword) throws BasicBudgetBuilderException {
        Map<String, String> errors = Maps.newHashMap();
        User user = userRepository.findByEmail(principal.getName());
        if (user == null) {
            errors.put("general", "Invalid User");
        }
        if (password == null || password.isEmpty()) {
            errors.put("password", "Required");
        }
        if (confirmPassword == null || confirmPassword.isEmpty()) {
            errors.put("confirmPassword", "Required");
        }
        if (password != null && confirmPassword != null
                && !password.equals(confirmPassword)) {
            errors.put("confirmPassword", "Password Does not match!");
        }
        if (!errors.isEmpty()) {
            throw new BasicBudgetBuilderException(errors);
        }
        try {
            user.setPassword(bCryptPasswordEncoder.encode(password));
            userRepository.save(user);
        } catch (DataAccessException e) {
            errors.put("general", e.getMessage());
            throw new BasicBudgetBuilderException(errors);
        }
        SecurityContextHolder.getContext().setAuthentication(null);
        passwordTokenRepository.deleteByUserId(user.getId());
        return "password has been successfully changed";
    }
    /** create a reset password token */
    public void createPasswordTokenForUser(User user, String token) {
        PasswordResetToken myToken = new PasswordResetToken(token, user);
        passwordTokenRepository.save(myToken);
    }
    /** create the reset password email message */
    private SimpleMailMessage constructResetTokenEmail(String contextPath, Locale locale, String token, User user) {
        String url = contextPath + "/changePassword?id=" +
                user.getId() + "&token=" + token;
        String message = "Click link to reset your password";
        return constructEmail("Reset Password", message + " \r\n" + url, user);
    }
    /** create a simple email message */
    private SimpleMailMessage constructEmail(String subject, String body, User user) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setSubject(subject);
        email.setText(body);
        email.setTo(user.getEmail());
        email.setFrom(environment.getProperty("support.email"));
        return email;
    }
    /** validates the password reset token */
    public String validatePasswordResetToken(long id, String token) {
        try {
            PasswordResetToken passToken =
                    passwordTokenRepository.findByToken(token);

            if ((passToken == null) || (passToken.getUser()
                    .getId() != id)) {
                return "invalidToken";
            }
            Calendar cal = Calendar.getInstance();
            if ((passToken.getExpiryDate()
                    .getTime() - cal.getTime()
                    .getTime()) <= 0) {
                return "expired";
            }
            User user = passToken.getUser();
            Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
            Authentication auth = new UsernamePasswordAuthenticationToken(
                    new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), grantedAuthorities),
                    null, Arrays.asList(
                    new SimpleGrantedAuthority("CHANGE_PASSWORD_PRIVILEGE")));
            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (Exception exp) {
            exp.printStackTrace();
            return "invalidToken";
        }
        return null;
    }
    /** creates the un-categorized budget for new user */
    private void createUncategorizedBudget(User user) throws BasicBudgetBuilderException {
        BudgetRep budgetRep = new BudgetRep("Uncategorized", "#ca0111",
                "Uncategorized budget for item deos not set budget yet",
                BigDecimal.valueOf(10000), Interval.YEAR,
                new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        budgetService.create(budgetRep, user);
    }
}