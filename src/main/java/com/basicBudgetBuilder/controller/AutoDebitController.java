package com.basicBudgetBuilder.controller;

import com.basicBudgetBuilder.domain.User;
import com.basicBudgetBuilder.exceptions.BasicBudgetBuilderException;
import com.basicBudgetBuilder.representation.AutoDebitRep;
import com.basicBudgetBuilder.representation.BudgetRep;
import com.basicBudgetBuilder.service.AutoDebitService;
import com.basicBudgetBuilder.service.UserService;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * Created by Hanzi Jing on 16/04/2017.
 */
@RestController
public class AutoDebitController {

    @Autowired
    private AutoDebitService autoDebitService;

    @Autowired
    private UserService userService;

    /**
     * GET /autoDebit - returns all autoDebits
     *
     * @param httpSession The current session
     * @return list of Budget representations and status 200, or error messages and status 400
     */
    @RequestMapping(value = "/autoDebitGet", method = RequestMethod.POST)
    public ResponseEntity<Object> getAll(@RequestBody List<BudgetRep> selectedBudgets, HttpSession httpSession){
        try{
            // final security check for session information
            User user = userService.getUserFromSession(httpSession);
            if (user == null){
                Map<String, String> errors = Maps.newHashMap();
                errors.put("session", "Session Expired");
                throw new BasicBudgetBuilderException(errors);
            }
            List<AutoDebitRep> autoDebitReps = autoDebitService.getAllForCategories(user, selectedBudgets);
            return new ResponseEntity<>(autoDebitReps, HttpStatus.OK);
        } catch (BasicBudgetBuilderException exp) {
            return new ResponseEntity<>(exp.getErrors(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * POST/autoDebit - Depending on whether an entry was selected an autodebit entry will be created or edited
     *
     * @param autoDebitRep What the autoDebitRep entry will change into/ be added
     * @param httpSession The current session
     * @return The new budget representation and status 200, or error messages and status 400
     */
    @RequestMapping(value = "/autoDebit", method = RequestMethod.POST)
    public ResponseEntity<Object> create(@RequestBody AutoDebitRep autoDebitRep, HttpSession httpSession) {
        try {
            User user = userService.getUserFromSession(httpSession);
            if (user == null){
                Map<String, String> errors = Maps.newHashMap();
                errors.put("session", "Session Expired");
                throw new BasicBudgetBuilderException(errors);
            }
            if (autoDebitRep.getId() <= 0){
                autoDebitRep = autoDebitService.create(autoDebitRep, user);
            }
            else {
                autoDebitRep = autoDebitService.edit(autoDebitRep, user);
            }
            return new ResponseEntity<>(autoDebitRep, HttpStatus.OK);
        } catch (BasicBudgetBuilderException exp) {
            return new ResponseEntity<>(exp.getErrors(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * DELETE /autoDebit - deletes the selected autoDebit
     *
     * @param id the auto-debit id that will be removed
     * @param httpSession The current session
     * @return status 200, or error messages and status 400
     */
    @RequestMapping(value = "/autoDebit", method = RequestMethod.DELETE)
    public ResponseEntity<Object> deleteChar(@RequestParam long id, HttpSession httpSession) {
        try {
            User user = userService.getUserFromSession(httpSession);
            if (user == null){
                Map<String, String> errors = Maps.newHashMap();
                errors.put("session", "Session Expired");
                return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
            }
            autoDebitService.delete(id);
            return new ResponseEntity<>(null, HttpStatus.OK);
        } catch (BasicBudgetBuilderException exp) {
            return new ResponseEntity<>(exp.getErrors(), HttpStatus.BAD_REQUEST);
        }
    }
}
