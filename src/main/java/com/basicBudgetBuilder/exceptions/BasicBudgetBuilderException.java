package com.basicBudgetBuilder.exceptions;

import com.google.common.collect.Maps;
import java.util.Map;

/**
 * Setup for all Exceptions thrown by the application used for parsing errors for controller
 * Created by Hanzi Jing on 14/04/2017.
 */
public class BasicBudgetBuilderException extends Exception{
    private final Map<String, String> errors = Maps.newHashMap();
    public BasicBudgetBuilderException(Map<String, String> errors){
        super();
        if (errors != null){
            this.errors.putAll(errors);
        }
    }
    public Map<String, String> getErrors() {
        return errors;
    }
}
