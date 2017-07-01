package com.basicBudgetBuilder.controller;

import com.basicBudgetBuilder.domain.User;
import com.basicBudgetBuilder.exceptions.BasicBudgetBuilderException;
import com.basicBudgetBuilder.representation.BudgetRep;
import com.basicBudgetBuilder.representation.StatRep;
import com.basicBudgetBuilder.service.StatisticsService;
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
 * Created by Hanzi Jing on 20/04/2017.
 */
@RestController
public class StatisticsController {
    @Autowired
    private StatisticsService statisticsService;

    @Autowired
    private UserService userService;

    /**
     * POST /statistics - gets all statistical data relevant to the selected budget categories
     *
     * @param budgetReps List of budget representations
     * @param httpSession The active Session
     * @return a representation object containing all the statistical information and status 200, or error messages and status 400
     */
    @RequestMapping(value = "/statistics", method = RequestMethod.POST)
    public ResponseEntity<Object> getStats(@RequestBody List<BudgetRep> budgetReps, HttpSession httpSession){
        try{
            User user = userService.getUserFromSession(httpSession);
            if (user == null){
                Map<String, String> errors = Maps.newHashMap();
                errors.put("session", "Session Expired");
                throw new BasicBudgetBuilderException(errors);
            }
            StatRep stats = statisticsService.getStats(user, budgetReps);
            return new ResponseEntity<>(stats, HttpStatus.OK);
        } catch (BasicBudgetBuilderException exp) {
            return new ResponseEntity<>(exp.getErrors(), HttpStatus.BAD_REQUEST);
        }
    }
}
