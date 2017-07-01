package com.basicBudgetBuilder.controller;

/**
 * Created by Hanzi Jing on 4/04/2017.
 */

import com.basicBudgetBuilder.domain.User;
import com.basicBudgetBuilder.exceptions.BasicBudgetBuilderException;
import com.basicBudgetBuilder.representation.UserRep;
import com.basicBudgetBuilder.service.UserService;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * GET /user - build and return a user Automatically (also acts as a security check)
     *
     * @param user the user currently active
     * @return the user
     */
    @RequestMapping("/user")
    public Principal user(Principal user) {
        return user;
    }

    /**
     * POST /registration - Saves a user into the database
     *
     * @param userRep the object containing the data to be included in the database
     * @param user1 placeholder of the active session (needed for function to work)
     * @return the user and status 200, or error messages and status 400
     */
    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public ResponseEntity<Object> saveUser(@RequestBody UserRep userRep, Principal user1){
        try{
            User user = userService.save(userRep);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (BasicBudgetBuilderException exp) {
            return new ResponseEntity<>(exp.getErrors(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * POST /resetPassword - requests for an email to be sent to the input e-mail address for containing a link
     *                      for resetting the password
     * @param email the user email address
     * @param request HttpServletRequest for getting the url of the request
     * @return a message reporting the success and status 200, or error messages and status 400
     */
    @RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Object> resetPassword(@RequestBody String email, HttpServletRequest request){
        try{
            Map<String, String>result = Maps.newHashMap();
            String message = userService.resetPassword(email, request);
            result.put("result", message);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (BasicBudgetBuilderException exp) {
            return new ResponseEntity<>(exp.getErrors(), HttpStatus.BAD_REQUEST);
        } catch(Exception exp){
            Map<String, String>errors = Maps.newHashMap();
            errors.put("general", exp.getMessage());
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     *  GET /resetPassword/changePassword - gets the token from the link from the sent e-mail to validate and redirects
     *                                      the user to the appropriate page
     * @param locale the locale (needed for function to work)
     * @param model the model  (needed for function to work)
     * @param id The ID of the user that sent the request
     * @param token The token generated from the e-mail request for password reset
     * @return redirection to the appropriate url
     */
    @RequestMapping(value = "/resetPassword/changePassword", method = RequestMethod.GET)
    public ModelAndView changePassword(Locale locale, Model model, @RequestParam("id") long id, @RequestParam("token") String token){
        String result = userService.validatePasswordResetToken(id, token);
        if (result != null) {
            return new ModelAndView("redirect:/#/login.html");
        }
        return new ModelAndView("redirect:/update_password.html");
    }

    /**
     *  POST /updatePassword - changes the password of the user
     *
     * @param passwords the password and confirm password
     * @param principal the user within the session
     * @return a message reporting the success and status 200, or error messages and status 400
     */
    @RequestMapping(value = "/updatePassword", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Object> updatePassword(@RequestBody List<String> passwords, Principal principal){
        try{
            if(principal == null){
                Map<String, String>errors = Maps.newHashMap();
                errors.put("general", "Session Expired");
                return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
            }
            userService.updatePassword(principal, passwords.get(0), passwords.get(1));
            return new ResponseEntity<>(null, HttpStatus.OK);
        } catch (BasicBudgetBuilderException exp) {
            return new ResponseEntity<>(exp.getErrors(), HttpStatus.BAD_REQUEST);
        } catch(Exception exp){
            Map<String, String>errors = Maps.newHashMap();
            errors.put("general", exp.getMessage());
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }
    }
}