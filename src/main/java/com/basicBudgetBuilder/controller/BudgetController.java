package com.basicBudgetBuilder.controller;

import com.basicBudgetBuilder.domain.User;
import com.basicBudgetBuilder.exceptions.BasicBudgetBuilderException;
import com.basicBudgetBuilder.representation.BudgetRep;
import com.basicBudgetBuilder.service.BudgetService;
import com.basicBudgetBuilder.service.UserService;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@RestController
public class BudgetController {
    @Autowired
    private BudgetService budgetService;

    @Autowired
    private UserService userService;

    /**
     * GET /budget- returns the selected all budgets
     *
     * @param httpSession The current session
     * @return list of Budget representations and status 200, or error messages and status 400
     */
    @RequestMapping(value = "/budget", method = RequestMethod.GET)
    public ResponseEntity<Object> getAll(HttpSession httpSession){
        try{
            User user = userService.getUserFromSession(httpSession);
            if (user == null){
                Map<String, String> errors = Maps.newHashMap();
                errors.put("session", "Session Expired");
                throw new BasicBudgetBuilderException(errors);
            }
            List<BudgetRep> budgetReps = budgetService.getAll(user);
            return new ResponseEntity<>(budgetReps, HttpStatus.OK);
        } catch (BasicBudgetBuilderException exp) {
            return new ResponseEntity<>(exp.getErrors(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * POST /budget - Depending on whether an entry was selected an budget entry will be created or edited
     *
     * @param budgetRep What the budget entry will change into/ be added
     * @param httpSession The current session
     * @return The new budget representation and status 200, or error messages and status 400
     */
    @RequestMapping(value = "/budget", method = RequestMethod.POST)
    public ResponseEntity<Object> create(@RequestBody BudgetRep budgetRep, HttpSession httpSession) {
        try {
            User user = userService.getUserFromSession(httpSession);
            if (user == null){
                Map<String, String> errors = Maps.newHashMap();
                errors.put("session", "Session Expired");
                throw new BasicBudgetBuilderException(errors);
            }
            if (budgetRep.getId() > 0){
                budgetRep = budgetService.edit(budgetRep, user);
            }
            else {
                budgetRep = budgetService.create(budgetRep, user);
            }
            return new ResponseEntity<>(budgetRep, HttpStatus.OK);
        } catch (BasicBudgetBuilderException exp) {
            return new ResponseEntity<>(exp.getErrors(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * DELETE /budget - deletes the selected budget
     *
     * @param id  selected budget entry
     * @return status 200, or error messages and status 400
     */
    @RequestMapping(value = "/budget", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<Object> deleteChar(@RequestParam long id, HttpSession httpSession) {
        try {
            User user = userService.getUserFromSession(httpSession);
            if (user == null){
                Map<String, String> errors = Maps.newHashMap();
                errors.put("session", "Session Expired");
                return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(budgetService.delete(id), HttpStatus.OK);
        } catch (BasicBudgetBuilderException exp) {
            return new ResponseEntity<>(exp.getErrors(), HttpStatus.BAD_REQUEST);
        }
    }
}
