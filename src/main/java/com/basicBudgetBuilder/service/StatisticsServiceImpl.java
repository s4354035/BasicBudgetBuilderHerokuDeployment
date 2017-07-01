package com.basicBudgetBuilder.service;

import com.basicBudgetBuilder.Utilities.DateUtil;
import com.basicBudgetBuilder.domain.Interval;
import com.basicBudgetBuilder.domain.User;
import com.basicBudgetBuilder.exceptions.BasicBudgetBuilderException;
import com.basicBudgetBuilder.repository.DebitCustomerRepository;
import com.basicBudgetBuilder.representation.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation of the Statistics Service
 * Created by Hanzi Jing on 10/04/2017.
 */
@Service
public class StatisticsServiceImpl implements StatisticsService {
    @Autowired
    private AutoDebitService autoDebitService;

    @Autowired
    private DebitCustomerRepository debitCustomerRepository;

    @Autowired
    private BudgetService budgetService;

    public StatRep getStats(User user, List<BudgetRep> budgetReps)throws BasicBudgetBuilderException{
        Map<String, String> errors = Maps.newHashMap();
        StatRep statRep = new StatRep();
        Map<Interval, Map<CategoryRep, Map<String, BudgetDebitRep>>>sixIntervalStat = Maps.newLinkedHashMap();
        try{
            List<BudgetRep> allBudgetReps = budgetService.getAll(user);
            Map<Interval, BigDecimal>restBudget = Maps.newLinkedHashMap();
            Map<Interval, BigDecimal>restSpent = Maps.newLinkedHashMap();
            if(budgetReps == null || budgetReps.isEmpty()){
                budgetReps= Lists.newArrayList(allBudgetReps);
            }
            // Get all selected categories
            List<CategoryRep>categoryReps = budgetReps.stream().map(budgetRep ->
                    new CategoryRep(budgetRep.getCategoryId(), budgetRep.getCategoryName(), budgetRep.getCategoryColour())).collect(Collectors.toList());
            // Get all fixed-cost for selected categories
            List<AutoDebitRep>autoDebitReps = autoDebitService.getAllForCategories(user, budgetReps);
            List<Long>catgoryIds = budgetReps.stream().map(BudgetRep::getCategoryId).collect(Collectors.toList());
            Map<Long, Map<Interval, BigDecimal>>budgetMaps = Maps.newHashMap();
            Map<Long, Map<Interval, BigDecimal>>budgetFixedCostMaps = Maps.newHashMap();
            Map<Interval, List<String>>dateMap = DateUtil.getSixFirstDateForInterval();

            // Calculate the budget for each interval type for selected categories
            budgetReps.forEach(budgetRep -> budgetMaps.put(budgetRep.getCategoryId(), DateUtil.getAmountMapForInterval(budgetRep.getBudgetInterval(), budgetRep.getAmount())));

            // Calculate fixed cost for each interval type for selected categories
            autoDebitReps.forEach(autoDebitRep -> {
                Map<Interval, BigDecimal> fixedMap = DateUtil.getAmountMapForInterval(autoDebitRep.getDebitInterval(), autoDebitRep.getAmount());
                if(!budgetFixedCostMaps.containsKey(autoDebitRep.getCategoryId())){
                    budgetFixedCostMaps.put(autoDebitRep.getCategoryId(), fixedMap);
                }
                else{
                    budgetFixedCostMaps.get(autoDebitRep.getCategoryId()).keySet().forEach(interval -> {
                        budgetFixedCostMaps.get(autoDebitRep.getCategoryId())
                                .put(interval,budgetFixedCostMaps.get(autoDebitRep.getCategoryId()).get(interval).add(fixedMap.get(interval)));
                    });
                }
            });

            // calculate the budget, fixed cost and debit for six intervals of each type for selected categories
            for (Interval interval : Interval.values()) {
                sixIntervalStat.put(interval, Maps.newLinkedHashMap());
                categoryReps.forEach(categoryRep -> {
                    BigDecimal budget = budgetMaps.get(categoryRep.getId()).get(interval);
                    BigDecimal fixedCost = budgetFixedCostMaps.containsKey(categoryRep.getId()) ?
                            budgetFixedCostMaps.get(categoryRep.getId()).get(interval) : BigDecimal.ZERO;
                    sixIntervalStat.get(interval).put(categoryRep, Maps.newLinkedHashMap());
                    dateMap.get(interval).forEach(dateString->{
                        BudgetDebitRep budgetDebitRep = new BudgetDebitRep();
                        budgetDebitRep.setBudget(budget);
                        budgetDebitRep.setDebit(fixedCost);
                        sixIntervalStat.get(interval).get(categoryRep).put(dateString, budgetDebitRep);
                    });
                });
                debitCustomerRepository.getSixIntervalSumByCategoryIdsAndInterval(user.getId(), catgoryIds, interval, sixIntervalStat.get(interval));
                statRep.getTotalBudget().put(interval, BigDecimal.ZERO);
                statRep.getTotalSpent().put(interval, debitCustomerRepository.getSumForInterval(user.getId(), interval));
            }

            // Calculate total budget
            allBudgetReps.forEach(budgetRep -> {
                Map<Interval, BigDecimal> amountMap = DateUtil.getAmountMapForInterval(budgetRep.getBudgetInterval(), budgetRep.getAmount());
                statRep.getTotalBudget().keySet().forEach(interval -> statRep.getTotalBudget().put(interval, statRep.getTotalBudget().get(interval).add(amountMap.get(interval))));
            });
            // Calculate total fixed cost and spending
            List<AutoDebitRep>allAutoDebitReps = autoDebitService.getAllForCategories(user, null);
            allAutoDebitReps.forEach(autoDebitRep -> {
                Map<Interval, BigDecimal> amountMap = DateUtil.getAmountMapForInterval(autoDebitRep.getDebitInterval(), autoDebitRep.getAmount());
                statRep.getTotalSpent().keySet().forEach(interval -> statRep.getTotalSpent().put(interval, statRep.getTotalSpent().get(interval).add(amountMap.get(interval))));
            });
            // Calculate the sum of unselected categories total
            for (Interval interval : Interval.values()) {
                statRep.getTotalRemainder().put(interval, statRep.getTotalBudget().get(interval).subtract(statRep.getTotalSpent().get(interval)).setScale(2, BigDecimal.ROUND_HALF_UP));
                restBudget.put(interval, statRep.getTotalBudget().get(interval));
                restSpent.put(interval, statRep.getTotalSpent().get(interval));
            }

            // format six interval data
            sixIntervalStat.keySet().forEach(interval -> {
                statRep.getCurrentBudgets().put(interval, Lists.newArrayList());
                statRep.getCurrentDebits().put(interval, Lists.newArrayList());
                statRep.getSixIntervalDebits().put(interval, Lists.newArrayList());
                String dateString = dateMap.get(interval).get(dateMap.get(interval).size() - 1);
                categoryReps.forEach(categoryRep ->{
                    List<BigDecimal>debits = Lists.newArrayList();
                    statRep.getSixIntervalDebits().get(interval).add(debits);
                    dateMap.get(interval).forEach(dateStr->{
                        BudgetDebitRep budgetDebitRep = sixIntervalStat.get(interval).get(categoryRep).get(dateStr);
                        debits.add(budgetDebitRep.getDebit());
                        if(dateStr.equals(dateString)){
                            statRep.getCurrentBudgets().get(interval).add(budgetDebitRep.getBudget());
                            statRep.getCurrentDebits().get(interval).add(budgetDebitRep.getDebit());
                            restBudget.put(interval, restBudget.get(interval).subtract(budgetDebitRep.getBudget()));
                            restSpent.put(interval, restSpent.get(interval).subtract(budgetDebitRep.getDebit()));
                        }
                    });
                });
            });
            for (Interval interval : Interval.values()) {
                statRep.getCurrentBudgets().get(interval).add(restBudget.get(interval));
                statRep.getCurrentDebits().get(interval).add(restSpent.get(interval));
            }
            statRep.getIntervalDates().putAll(dateMap);
            categoryReps.forEach(categoryRep -> {
                statRep.getCategoryNames().add(categoryRep.getName());
                statRep.getCategoryColours().add(categoryRep.getColour());
            });
            statRep.getCategoryNames().add("Rest");
            statRep.getCategoryColours().add(BudgetServiceImpl.REST_COLOUR);

            // calculate real remainder
            BigDecimal remainderPerDay = statRep.getTotalRemainder().get(Interval.YEAR)
                    .divide(BigDecimal.valueOf(DateUtil.getIntervalRestDays(Interval.YEAR)[0]), 2, BigDecimal.ROUND_HALF_UP);
            for (Interval interval : Interval.values()) {
                if(interval == Interval.YEAR){
                    statRep.getTotalRealRemainder().put(interval, statRep.getTotalRemainder().get(interval));
                }
                else{
                    int[] restDays = DateUtil.getIntervalRestDays(interval);
                    int diff = restDays[0] - restDays[1];
                    // end of this period is in the current year
                    if(diff <= 0){
                        statRep.getTotalRealRemainder().put(interval,remainderPerDay.multiply(BigDecimal.valueOf(restDays[1])));
                    }
                    else{
                        BigDecimal intervalRemainderPerDay = statRep.getTotalRemainder().get(interval).divide(BigDecimal.valueOf(restDays[0]),2, BigDecimal.ROUND_HALF_UP );
                        BigDecimal realRemainderEndOfYear = remainderPerDay.multiply(BigDecimal.valueOf(restDays[1]));
                        BigDecimal nextYearRemainerd = intervalRemainderPerDay.multiply(BigDecimal.valueOf(diff));
                        statRep.getTotalRealRemainder().put(interval, realRemainderEndOfYear.add(nextYearRemainerd));
                    }
                }
            }
        } catch (DataAccessException e){
            errors.put("general", e.getMessage());
            throw new BasicBudgetBuilderException(errors);
        }
        return statRep;
    }
}
