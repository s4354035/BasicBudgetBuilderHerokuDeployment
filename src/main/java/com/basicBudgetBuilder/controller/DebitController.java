package com.basicBudgetBuilder.controller;

import com.basicBudgetBuilder.domain.User;
import com.basicBudgetBuilder.exceptions.BasicBudgetBuilderException;
import com.basicBudgetBuilder.representation.BudgetRep;
import com.basicBudgetBuilder.representation.DebitRep;
import com.basicBudgetBuilder.service.DebitService;
import com.basicBudgetBuilder.service.UserService;
import com.google.common.collect.Lists;
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
public class DebitController {
    @Autowired
    private DebitService debitService;
    @Autowired
    private UserService userService;
    /**
     * GET /debit - returns the selected all debits
     *
     * @param httpSession The current session
     * @return list of debit representations and status 200, or error messages and status 400
     */
    @RequestMapping(value = "/debitAll", method = RequestMethod.POST)
    public ResponseEntity<Object> getAll(@RequestBody List<BudgetRep> budgetReps, HttpSession httpSession){
//        public ResponseEntity<Object> getAll(HttpSession httpSession){
        try{
            User user = userService.getUserFromSession(httpSession);
            if (user == null){
                Map<String, String> errors = Maps.newHashMap();
                errors.put("session", "Session Expired");
                throw new BasicBudgetBuilderException(errors);
            }
            List<DebitRep> debitReps = debitService.getByCategories(user, budgetReps);
            return new ResponseEntity<>(debitReps, HttpStatus.OK);
        } catch (BasicBudgetBuilderException exp) {
            return new ResponseEntity<>(exp.getErrors(), HttpStatus.BAD_REQUEST);
        }
    }
    /**
     * POST /debit - Depending on whether an entry was selected an debit entry will be created or edited
     *
     * @param debitRep What the debit entry will change into/ be added
     * @param httpSession The current session
     * @return The new debit representation and status 200, or error messages and status 400
     */
    @RequestMapping(value = "/debit", method = RequestMethod.POST)
    public ResponseEntity<Object> create(@RequestBody DebitRep debitRep, HttpSession httpSession) {
        try {
            User user = userService.getUserFromSession(httpSession);
            if (user == null){
                Map<String, String> errors = Maps.newHashMap();
                errors.put("session", "Session Expired");
                throw new BasicBudgetBuilderException(errors);
            }
            if (debitRep.getId() > 0){
                debitRep = debitService.edit(debitRep, user);
            }
            else {
                debitRep = debitService.create(debitRep, user);
            }
            return new ResponseEntity<>(debitRep, HttpStatus.OK);
        }  catch (BasicBudgetBuilderException exp) {
            return new ResponseEntity<>(exp.getErrors(), HttpStatus.BAD_REQUEST);
        }
    }
    /**
     * DELETE /debit - deletes the selected debit
     *
     * @param id selected debit entry ID
     * @return status 200, or error messages and status 400
     */
    @RequestMapping(value = "/debit", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<Object> deleteChar(@RequestParam long id, HttpSession httpSession) {
        try {
            User user = userService.getUserFromSession(httpSession);
            if (user == null){
                Map<String, String> errors = Maps.newHashMap();
                errors.put("session", "Session Expired");
                return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(debitService.delete(id), HttpStatus.OK);
        }  catch (BasicBudgetBuilderException exp) {
            return new ResponseEntity<>(exp.getErrors(), HttpStatus.BAD_REQUEST);
        }
    }
}
