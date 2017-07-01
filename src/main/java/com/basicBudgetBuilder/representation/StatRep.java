package com.basicBudgetBuilder.representation;

import com.basicBudgetBuilder.domain.Interval;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * All Statistics data for reports
 * Created by Hanzi Jing on 8/04/2017.
 */
@Data
public class StatRep {
    private final Map<Interval, BigDecimal> totalBudget;
    private final Map<Interval, BigDecimal> totalSpent;
    private final Map<Interval, BigDecimal> totalRemainder;
    private final Map<Interval, BigDecimal> totalRealRemainder;
    private final List<String> categoryNames;
    private final List<String> categoryColours;
    private final Map<Interval, List<BigDecimal>> currentBudgets;
    private final Map<Interval, List<BigDecimal>> currentDebits ;
    private final Map<Interval, List<String>>intervalDates;
    private final Map<Interval, List<List<BigDecimal>>>sixIntervalDebits;

    public StatRep(){
        totalBudget = Maps.newLinkedHashMap();
        totalSpent  = Maps.newLinkedHashMap();
        totalRealRemainder = Maps.newLinkedHashMap();
        totalRemainder = Maps.newLinkedHashMap();
        categoryNames = Lists.newArrayList();
        categoryColours = Lists.newArrayList();
        currentBudgets = Maps.newLinkedHashMap();
        currentDebits = Maps.newLinkedHashMap();
        intervalDates = Maps.newLinkedHashMap();
        sixIntervalDebits = Maps.newLinkedHashMap();
    }
}
