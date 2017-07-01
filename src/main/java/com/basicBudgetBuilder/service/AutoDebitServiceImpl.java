package com.basicBudgetBuilder.service;

import com.basicBudgetBuilder.Utilities.DateUtil;
import com.basicBudgetBuilder.domain.AutoDebit;
import com.basicBudgetBuilder.domain.Category;
import com.basicBudgetBuilder.domain.Interval;
import com.basicBudgetBuilder.domain.User;
import com.basicBudgetBuilder.exceptions.BasicBudgetBuilderException;
import com.basicBudgetBuilder.repository.AutoDebitCustomerRepository;
import com.basicBudgetBuilder.repository.AutoDebitRepository;
import com.basicBudgetBuilder.repository.CategoryRepository;
import com.basicBudgetBuilder.representation.AutoDebitRep;
import com.basicBudgetBuilder.representation.BudgetRep;
import com.basicBudgetBuilder.representation.DebitRep;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation of the AutoDebit Service
 * Created by Hanzi Jing on 10/04/2017.
 */
@Repository
@Service
public class AutoDebitServiceImpl implements AutoDebitService{
    @Autowired
    private AutoDebitRepository autoDebitRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private AutoDebitCustomerRepository  autoDebitCustomerRepository;

    /** get the map of autodebit amount by category for the selected budgets */
    public Map<Long, BigDecimal>getCostsForBudgets(User user, List<BudgetRep> budgetReps) throws BasicBudgetBuilderException{
        Map<Long, BigDecimal>costMap = Maps.newHashMap();
        Map<Long, Interval>bugetIntervalMap = Maps.newHashMap();
        budgetReps.forEach(budgetRep -> {
            if(budgetRep.getCategoryId() > 0 && budgetRep.getBudgetInterval() != null) {
                costMap.put(budgetRep.getCategoryId(), BigDecimal.ZERO);
                bugetIntervalMap.put(budgetRep.getCategoryId(), budgetRep.getBudgetInterval());
            }
        });
        List<AutoDebitRep>autoDebits = getAllForCategories(user, budgetReps);
        if(autoDebits != null && !autoDebits.isEmpty()){
            autoDebits.forEach(autoDebitRep -> {
                costMap.put(autoDebitRep.getCategoryId(), costMap.get(autoDebitRep.getCategoryId())
                        .add(DateUtil.getAmountForInterval(autoDebitRep.getDebitInterval(), bugetIntervalMap.get(autoDebitRep.getCategoryId()), autoDebitRep.getAmount())));

            });
        }
        return costMap;
    }
    /**
     * get all autoDebit entries that are from the list of budgetRep categories
     *
     * @param user       required
     * @param budgetReps required
     * @return the list of autoDebit representations
     * @throws BasicBudgetBuilderException Exception Description
     */
    public List<AutoDebitRep> getAllForCategories(User user, List<BudgetRep> budgetReps) throws BasicBudgetBuilderException {
        Map<String, String> errors = Maps.newHashMap();
        try{
            List<AutoDebit> autoDebits = budgetReps == null || budgetReps.isEmpty() ?
                    autoDebitRepository.findByUser(user) :
                    autoDebitCustomerRepository.findAutoDebitByCategory(user.getId(),
                            budgetReps.stream().map(a->Long.valueOf(a.getCategoryId())).collect(Collectors.toList()));
            List<AutoDebitRep> autoDebitReps = Lists.newArrayList();
            for (AutoDebit autoDebit:autoDebits) {
                AutoDebitRep autoDebitRep = new AutoDebitRep(
                        autoDebit.getId(),
                        user.getId(),
                        autoDebit.getCategory().getId(),
                        autoDebit.getCategory().getName(),
                        autoDebit.getCategory().getColour(),
                        autoDebit.getDescription(),
                        autoDebit.getAmount(),
                        autoDebit.getDebitInterval());
                autoDebitReps.add(autoDebitRep);
            }
            return autoDebitReps;
        }catch (DataAccessException e){
            errors.put("general", e.getMessage());
            throw new BasicBudgetBuilderException(errors);
        }
    }
    /**
     * Adds a autoDebit to the database
     *
     * @param autoDebitRep required
     * @param user         required
     * @return the Representation of the autoDebit that was added to the database with the user and category IDs
     * @throws BasicBudgetBuilderException Exception Description
     */
    public AutoDebitRep create(AutoDebitRep autoDebitRep, User user) throws BasicBudgetBuilderException {
        Map<String, String> errors = Maps.newHashMap();
        try{
            Category category = validate(autoDebitRep, user);
            AutoDebit autoDebit = new AutoDebit(
                    autoDebitRep.getDescription(),
                    autoDebitRep.getAmount(),
                    autoDebitRep.getDebitInterval(),
                    user,
                    category);
            autoDebit = autoDebitRepository.save(autoDebit);
            autoDebitRep.setId(autoDebit.getId());
            autoDebitRep.setUserId(autoDebit.getUser().getId());
            return autoDebitRep;
        }catch (DataAccessException e){
            errors.put("general", e.getMessage());
            throw new BasicBudgetBuilderException(errors);
        }
    }
    /**
     * Edits a autoDebit from the database
     *
     * @param autoDebitRep required
     * @param user         required
     * @return the Representation of the edited autoDebit
     * @throws BasicBudgetBuilderException Exception Description
     */
    public AutoDebitRep edit(AutoDebitRep autoDebitRep, User user)throws BasicBudgetBuilderException {
        Map<String, String> errors = Maps.newHashMap();
        try{
            Category category = validate(autoDebitRep, user);
            AutoDebit autoDebit = new AutoDebit(
                    autoDebitRep.getId(),
                    autoDebitRep.getDescription(),
                    autoDebitRep.getAmount(),
                    autoDebitRep.getDebitInterval(),
                    category);
            autoDebit.setUser(user);
            autoDebit = autoDebitRepository.save(autoDebit);
            autoDebitRep.setId(autoDebit.getId());
            autoDebitRep.setUserId(autoDebit.getUser().getId());
            return autoDebitRep;
        }catch (DataAccessException e){
            errors.put("general", e.getMessage());
            throw new BasicBudgetBuilderException(errors);
        }
    }
    /**
     * Deletes the selected autoDebit from the database
     *
     * @param id required
     * @return True if delete success, or false
     */
    public Boolean delete(long id) throws BasicBudgetBuilderException{
        Map<String, String> errors = Maps.newHashMap();
        try{
            autoDebitRepository.delete(id);
        } catch (Exception e){
            errors.put("general", e.getMessage());
            throw new BasicBudgetBuilderException(errors);
        }
        return true;
    }
    /**
     * Validates whether the input is correct and returns the category necessary for the creation
     *
     * @param autoDebitRep required
     * @param user         required
     * @return The Category of the new autoDebit
     * @throws BasicBudgetBuilderException Exception Description
     */
    private Category validate(AutoDebitRep autoDebitRep, User user)throws BasicBudgetBuilderException {
        Map<String, String> errors = Maps.newHashMap();
        Category category = null;
        if (autoDebitRep == null){
            errors.put("general","No Input Data");
            throw new BasicBudgetBuilderException(errors);
        }
        if (autoDebitRep.getCategoryName() == null ||
                autoDebitRep.getCategoryName().trim().isEmpty() ||
                categoryRepository.findByNameAndUser(autoDebitRep.getCategoryName(), user) == null){
            category = categoryRepository.findByNameAndUser("Uncategorized", user);
            autoDebitRep.setCategoryName(category.getName());
            autoDebitRep.setCategoryId(category.getId());
            autoDebitRep.setCategoryColour(category.getColour());
        }
        else{
            category = categoryRepository.findByNameAndUser(autoDebitRep.getCategoryName(), user);
            autoDebitRep.setCategoryName(category.getName());
            autoDebitRep.setCategoryId(category.getId());
            autoDebitRep.setCategoryColour(category.getColour());
        }
        if (autoDebitRep.getAmount()==null || autoDebitRep.getAmount().doubleValue() <= 0){
            errors.put("amount", "must be greater than zero");
        }
        if (autoDebitRep.getDebitInterval() == null){
            errors.put("budgetInterval", "Required");
        }
        if (!errors.isEmpty()){
            throw new BasicBudgetBuilderException(errors);
        }
        return category;
    }
}