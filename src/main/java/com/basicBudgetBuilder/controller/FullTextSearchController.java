package com.basicBudgetBuilder.controller;

import com.basicBudgetBuilder.domain.User;
import com.basicBudgetBuilder.exceptions.BasicBudgetBuilderException;
import com.basicBudgetBuilder.representation.FullTextSearchEntity;
import com.basicBudgetBuilder.service.FullTextSearchService;
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
public class FullTextSearchController {

    @Autowired
    private FullTextSearchService searchService;

    @Autowired
    private UserService userService;

    /**
     * GET /fullTextSearch - returns a list of all entries
     *                       for budget, debit and autoDebit that is relevant to the enquiry
     * @param httpSession The current session
     * @return list of search results and status 200, or error messages and status 400
     */
    @RequestMapping(value = "/fullTextSearch", method = RequestMethod.POST)
    public ResponseEntity<Object> fullTextSearch(@RequestBody String text, HttpSession httpSession){
        try{
            User user = userService.getUserFromSession(httpSession);
            if (user == null){
                Map<String, String> errors = Maps.newHashMap();
                errors.put("session", "Session Expired");
                throw new BasicBudgetBuilderException(errors);
            }
            List<FullTextSearchEntity>results = searchService.fullTextSearch(user, text);
            return new ResponseEntity<>(results, HttpStatus.OK);
        }  catch (BasicBudgetBuilderException exp) {
            return new ResponseEntity<>(exp.getErrors(), HttpStatus.BAD_REQUEST);
        }
    }
}
