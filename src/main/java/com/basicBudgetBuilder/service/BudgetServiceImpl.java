package com.basicBudgetBuilder.service;

import com.basicBudgetBuilder.domain.*;
import com.basicBudgetBuilder.exceptions.BasicBudgetBuilderException;
import com.basicBudgetBuilder.repository.*;
import com.basicBudgetBuilder.representation.BudgetRep;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.Map;

/**
 * Implementation of the Budget Service
 * Created by Hanzi Jing on 9/04/2017.
 */
@Repository
@Service
public class BudgetServiceImpl implements BudgetService {
    public static final String REST_COLOUR = "#AAAAAA";
    @Autowired
    private BudgetRepository budgetRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private BudgetCustomerRepository budgetCustomerRepository;
    @Autowired
    private DebitCustomerRepository debitCustomerRepository;
    @Autowired
    private AutoDebitCustomerRepository autoDebitCustomerRepository;
    @Autowired
    private AutoDebitService autoDebitService;
    @Autowired
    private DebitService debitService;
    /**
     * Retrieves the most recent budget entry for each category
     *
     * @param user required
     * @return List of Budget Representations
     * @throws BasicBudgetBuilderException Exception Description
     */
    public List<BudgetRep> getAll(User user) throws BasicBudgetBuilderException {
        Map<String, String> errors = Maps.newHashMap();
        try {
            List<Budget> budgets = budgetCustomerRepository.findAllByUserId(user.getId());
            List<BudgetRep> budgetReps = Lists.newArrayList();
            for (Budget budget : budgets) {
                BigDecimal spent = debitService.getCurrentSpending(user, budget);
                BudgetRep budgetRep = new BudgetRep(
                        budget.getId(),
                        user.getId(),
                        budget.getCategory().getId(),
                        budget.getCategory().getName(),
                        budget.getCategory().getColour(),
                        budget.getDescription(),
                        budget.getAmount(),
                        budget.getBudgetInterval(),
                        budget.getEffectiveDate().toString());
                budgetRep.setSpent(spent);
                budgetReps.add(budgetRep);
            }
            Map<Long, BigDecimal> fixedCosts = autoDebitService.getCostsForBudgets(user, budgetReps);
            budgetReps.forEach(budgetRep -> {
                if (fixedCosts.containsKey(budgetRep.getCategoryId())) {
                    if (budgetRep.getSpent() != null) {
                        budgetRep.setSpent(budgetRep.getSpent().add(fixedCosts.get(budgetRep.getCategoryId())));
                    }
                }
            });
            return budgetReps;
        } catch (DataAccessException e) {
            errors.put("general", e.getMessage());
            throw new BasicBudgetBuilderException(errors);
        }
    }
    /**
     * Adds a budget to the database
     *
     * @param budgetRep required
     * @param user      required
     * @return the Representation of the budget that was added to the database with the user and category IDs
     * @throws BasicBudgetBuilderException Exception Description
     */
    public BudgetRep create(BudgetRep budgetRep, User user) throws BasicBudgetBuilderException {
        Map<String, String> errors = Maps.newHashMap();
        try {
            Category category = createValidate(budgetRep, user);
            if (category == null) {
                category = new Category(budgetRep.getCategoryName(), budgetRep.getCategoryColour(), user);
                addCategory(category);
            } else {
                category.setColour(budgetRep.getCategoryColour());
            }
            Budget budget = new Budget(
                    budgetRep.getDescription(),
                    budgetRep.getAmount(),
                    budgetRep.getBudgetInterval(),
                    Date.valueOf(budgetRep.getEffectiveDate()),
                    user,
                    category);
            budget = budgetRepository.save(budget);
            budgetRep.setId(budget.getId());
            budgetRep.setUserId(budget.getUser().getId());
            budgetRep.setCategoryId(budget.getCategory().getId());
            return budgetRep;
        } catch (DataAccessException e) {
            errors.put("general", e.getMessage());
            throw new BasicBudgetBuilderException(errors);
        }
    }
    /**
     * Edits a budget from the database
     *
     * @param budgetRep required
     * @param user      required
     * @return the Representation of the edited budget
     * @throws BasicBudgetBuilderException Exception Description
     */
    public BudgetRep edit(BudgetRep budgetRep, User user) throws BasicBudgetBuilderException {
        Map<String, String> errors = Maps.newHashMap();
        try {
            Category category = editValidate(budgetRep, user);
            if (category == null) {
                category = new Category(budgetRep.getCategoryName(), budgetRep.getCategoryColour(), user);
                addCategory(category);
            } else {
                category.setColour(budgetRep.getCategoryColour());
            }
            Budget budget = new Budget(
                    budgetRep.getId(),
                    budgetRep.getDescription(),
                    budgetRep.getAmount(),
                    budgetRep.getBudgetInterval(),
                    Date.valueOf(budgetRep.getEffectiveDate()),
                    category);
            budget.setUser(user);
            budget = budgetRepository.save(budget);
            budgetRep.setUserId(budget.getUser().getId());
            budgetRep.setCategoryId(budget.getCategory().getId());
            return budgetRep;
        } catch (DataAccessException e) {
            errors.put("general", e.getMessage());
            throw new BasicBudgetBuilderException(errors);
        }
    }
    /**
     * Deletes the selected budget from the database
     *
     * @param id required
     * @return True if delete success, or false
     */
    public Boolean delete(long id) throws BasicBudgetBuilderException {
        Map<String, String> errors = Maps.newHashMap();
        try {
            Budget budget = budgetRepository.findById(id);
            if (hasDebits(budget.getUser().getId(), budget.getCategory().getId())) {
                errors.put("delete", "Budget entry has debits assigned to it");
                throw new BasicBudgetBuilderException(errors);
            }
            budgetRepository.delete(id);

            return true;
        } catch (DataAccessException e) {
            errors.put("general", e.getMessage());
            throw new BasicBudgetBuilderException(errors);
        }
    }
    /**
     * checks if the selected budget has debit entries that are dependent on it
     *
     * @param userId     required
     * @param categoryId required
     * @return true if there debits dependent, otherwise false
     * @throws BasicBudgetBuilderException
     */
    private boolean hasDebits(long userId, long categoryId) throws BasicBudgetBuilderException {
        Map<String, String> errors = Maps.newHashMap();
        try {
            List<Debit> debits = debitCustomerRepository.findDebitByCategory(userId, categoryId);
            List<AutoDebit>autoDebits = autoDebitCustomerRepository.findAutoDebitByCategory(userId, Lists.newArrayList(categoryId));
            return ((debits != null && !debits.isEmpty()) || (autoDebits != null && !autoDebits.isEmpty())) ;
        } catch (DataAccessException e) {
            errors.put("general", e.getMessage());
            throw new BasicBudgetBuilderException(errors);
        }
    }
    /**
     * Creates a new category to the database
     *
     * @param category required
     * @return The new category
     * @throws BasicBudgetBuilderException Exception Description
     */
    private Category addCategory(Category category) throws BasicBudgetBuilderException {
        Map<String, String> errors = Maps.newHashMap();
        try {
            return categoryRepository.save(category);
        } catch (DataAccessException e) {
            errors.put("general", e.getMessage());
            throw new BasicBudgetBuilderException(errors);
        }
    }
    /**
     * Validates whether the input is correct and returns the category necessary for the creation
     *
     * @param budgetRep required
     * @param user      required
     * @return The Category of the new budget
     * @throws BasicBudgetBuilderException Exception Description
     */
    private Category createValidate(BudgetRep budgetRep, User user) throws BasicBudgetBuilderException {
        Map<String, String> errors = Maps.newHashMap();
        Category category = null;
        if (budgetRep == null) {
            errors.put("general", "No Input Data");
            throw new BasicBudgetBuilderException(errors);
        }
        if (budgetRep.getCategoryName() == null || budgetRep.getCategoryName().trim().isEmpty()) {
            errors.put("categoryName", "Required");
        } else {
            category = categoryRepository.findByNameAndUser(budgetRep.getCategoryName().trim(), user);
        }
        if (budgetRep.getAmount().floatValue() <= 0) {
            errors.put("amount", "Must be greater than zero");
        }
        if (budgetRep.getBudgetInterval() == null) {
            errors.put("budgetInterval", "Required");
        }
        if (budgetRep.getEffectiveDate() == null) {
            errors.put("effectiveDate", "Required");
        } else if (category != null) {
            List<Budget> budgets = budgetRepository.findByCategoryAndEffectiveDateAndUser(category, Date.valueOf(budgetRep.getEffectiveDate()), user);
            if (budgets != null && !budgets.isEmpty()) {
                errors.put("general", "Budget " + category.getName() + " with Effective Date: " +
                        budgetRep.getEffectiveDate() + " Already Exists");
            }
        }
        if (budgetRep.getCategoryColour() == null || budgetRep.getCategoryColour().trim().isEmpty()) {
            errors.put("categoryColour", "Required");
        } else if (!budgetRep.getCategoryColour().matches("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$")) {
            errors.put("categoryColour", "Illegal Colour Expression");
        } else {
            if(REST_COLOUR.equalsIgnoreCase(budgetRep.getCategoryColour())){
                errors.put("categoryColour", "This Colour is already Used by another category");
            }
            else {
                Category category1 = categoryRepository.findByColourAndUser(budgetRep.getCategoryColour(), user);
                if (category1 != null && (category == null || !category1.getName().equals(category.getName()))) {
                    errors.put("categoryColour", "This Colour is already Used by another category");
                }
            }
        }
        if (!errors.isEmpty()) {
            throw new BasicBudgetBuilderException(errors);
        }
        return category;
    }
    /**
     * Validates whether the input is correct and returns the category necessary for the edit
     *
     * @param budgetRep required
     * @param user      required
     * @return The Category of the edited budget
     * @throws BasicBudgetBuilderException Exception Description
     */
    private Category editValidate(BudgetRep budgetRep, User user) throws BasicBudgetBuilderException {
        Map<String, String> errors = Maps.newHashMap();
        Budget oldBudget;
        Category category;
        if (budgetRep == null) {
            errors.put("general", "No Input Data");
            throw new BasicBudgetBuilderException(errors);
        }
        if (budgetRep.getId() <= 0) {
            errors.put("general", "Selected budget no longer exists");
            throw new BasicBudgetBuilderException(errors);
        } else {
            oldBudget = budgetRepository.findById(budgetRep.getId());
            category = oldBudget.getCategory();
        }
        if (budgetRep.getCategoryName() == null || budgetRep.getCategoryName().trim().isEmpty()) {
            errors.put("categoryName", "Required");
        }
        if (budgetRep.getCategoryName() != null && !budgetRep.getCategoryName().equals(category.getName())) {
            category = null;
        }
        if (budgetRep.getAmount().floatValue() <= 0) {
            errors.put("amount", "Must be greater than zero");
        }
        if (budgetRep.getBudgetInterval() == null) {
            errors.put("budgetInterval", "Required");
        }
        if (budgetRep.getEffectiveDate() == null) {
            errors.put("effectiveDate", "Required");
        } else if (category != null) {
            List<Budget> budgets = budgetRepository.findByCategoryAndEffectiveDateAndUser(category, Date.valueOf(budgetRep.getEffectiveDate()), user);
            boolean valid = false;
            if (budgets != null && !budgets.isEmpty()) {
                for (Budget budget : budgets) {
                    if (budget.getId() == oldBudget.getId()) {
                        valid = true;
                        break;
                    }
                }
                if (!valid) {
                    errors.put("general", "Budget " + category.getName() + " with Effective Date: " +
                            budgetRep.getEffectiveDate() + " Already Exists");
                }
            }
        }
        if (budgetRep.getCategoryColour() == null || budgetRep.getCategoryColour().trim().isEmpty()) {
            errors.put("categoryColour", "Required");
        } else if (!budgetRep.getCategoryColour().matches("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$")) {
            errors.put("categoryColour", "Illegal Colour Expression");
        } else {
            Category category1 = categoryRepository.findByColourAndUser(budgetRep.getCategoryColour(), user);
            if (category1 != null && (category == null || (!category1.getName().equals(category.getName()) && !category1.equals(oldBudget.getCategory())))) {
                errors.put("categoryColour", "This Colour is already Used by another category");
            }
        }
        if (!errors.isEmpty()) {
            throw new BasicBudgetBuilderException(errors);
        }
        return category;
    }
}
