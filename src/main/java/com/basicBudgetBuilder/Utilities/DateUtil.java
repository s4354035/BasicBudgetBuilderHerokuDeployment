package com.basicBudgetBuilder.Utilities;

import com.basicBudgetBuilder.domain.Interval;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Utilities for data related
 */
public class DateUtil {

    /**
     * Convert date into yyyy-mm--dd format of string
     * @param date the input date
     * @return the formatted date string
     */
    public static String dateString(Date date) {
        if (date == null) return "";
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    /**
     *  Get the days to end of the input period and the days to end of the period within current year
     * @param interval the input interval
     * @return day[0] days to end of this interval, day[1] days to end of this interval within current year
     */
    public static int[] getIntervalRestDays(Interval interval) {

        int[] days = new int[2];
        Calendar cal = Calendar.getInstance();
        days[1] = 365 - cal.get(Calendar.DAY_OF_YEAR) + 1;
        switch (interval) {
            case WEEK:
                days[0] = 7 - cal.get(Calendar.DAY_OF_WEEK) + 1;
                break;
            case FORTNIGHT:
                int week = cal.get(Calendar.WEEK_OF_YEAR);
                int fortnightStartWeek =  (int)Math.ceil(week/2.0) * 2 - 1;
                days[0] = week > fortnightStartWeek ?  7 - cal.get(Calendar.DAY_OF_WEEK) + 1 : 7 - cal.get(Calendar.DAY_OF_WEEK) + 8;
                break;
            case MONTH:
                days[0] = cal.getActualMaximum(Calendar.DAY_OF_MONTH) - cal.get(Calendar.DAY_OF_MONTH) + 1;
                break;
            case QUARTER:
                int month = cal.get(Calendar.MONTH);
                days[0] = cal.getActualMaximum(Calendar.DAY_OF_MONTH) - cal.get(Calendar.DAY_OF_MONTH) + 1;
                int quarterStartMonth = (cal.get(Calendar.MONTH) / 3) * 3;
                int n = month - quarterStartMonth;
                while(n > 0){
                    month++;
                    n--;
                    cal.set(Calendar.MONTH, month);
                    days[0] += cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                }
                break;
            case YEAR:
                days[0] = 365 - cal.get(Calendar.DAY_OF_YEAR) + 1;
                break;
            default:
                break;
        }
        if(days[1] > days[0]){
            days[1] = days[0];
        }
        return days;
    }

    /**
     *  get each interval start date - for database queries
     * @param interval
     * @return
     */
    public static String getIntervalStartDate(Interval interval) {
        Calendar cal = Calendar.getInstance();
        int unit;
        String dateString = "";
        switch (interval) {
            case WEEK:
                unit = cal.get(Calendar.WEEK_OF_YEAR);
                cal.set(Calendar.WEEK_OF_YEAR, unit);
                cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                dateString = dateString(cal.getTime());
                break;
            case FORTNIGHT:
                unit = cal.get(Calendar.WEEK_OF_YEAR);
                unit = (int)Math.ceil(unit/2.0) * 2 - 1;
                cal.set(Calendar.WEEK_OF_YEAR, unit);
                cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                dateString = dateString(cal.getTime());
                break;
            case MONTH:
                unit = cal.get(Calendar.MONTH);
                cal.set(Calendar.MONTH, unit);
                cal.set(Calendar.DAY_OF_MONTH, 1);
                dateString = dateString(cal.getTime());
                break;
            case QUARTER:
                unit = (cal.get(Calendar.MONTH) / 3) * 3;
                cal.set(Calendar.MONTH, unit);
                cal.set(Calendar.DAY_OF_MONTH, 1);
                dateString = dateString(cal.getTime());
                break;
            case YEAR:
                unit = cal.get(Calendar.YEAR);
                cal.set(Calendar.YEAR, unit);
                cal.set(Calendar.DAY_OF_YEAR, 1);
                dateString = dateString(cal.getTime());
                break;
            default:
                break;
        }
        return dateString;
    }

    /**
     * get the start date for six time intervals
     * @return
     */
    public static Map<Interval, List<String>> getSixFirstDateForInterval() {
        Map<Interval, List<String>> dateMap = Maps.newHashMap();
        Calendar cal = Calendar.getInstance();
        int unit = cal.get(Calendar.WEEK_OF_YEAR);
        for (Interval interval : Interval.values()) {
            dateMap.put(interval, Lists.newArrayList());
        }

        for (int i = 0; i < 6; i++) {
            cal.set(Calendar.WEEK_OF_YEAR, unit);
            cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
            dateMap.get(Interval.WEEK).add(0, dateString(cal.getTime()));
            if(unit <= 1){
                int day = 31;
                if(cal.get(Calendar.MONTH) == 0) {
                    cal.set(cal.get(Calendar.YEAR) - 1, 11, day);
                }
                unit = cal.get(Calendar.WEEK_OF_YEAR);
                while(unit <= 1){ //test for previous year entries
                    day--;
                    cal.set(cal.get(Calendar.YEAR), 11, day);
                    unit = cal.get(Calendar.WEEK_OF_YEAR);
                }
            }
            unit--;
        }

        cal = Calendar.getInstance();
        unit = cal.get(Calendar.WEEK_OF_YEAR);
        for (int i = 0; i < 6; i++) {
            unit = (int)Math.ceil(unit/2.0) * 2 - 1;
            cal.set(Calendar.WEEK_OF_YEAR, unit);
            cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
            dateMap.get(Interval.FORTNIGHT).add(0, dateString(cal.getTime()));
            if(unit <= 1){
                int day = 31;
                if(cal.get(Calendar.MONTH) == 0) {
                    cal.set(cal.get(Calendar.YEAR) - 1, 11, day);
                }
                unit = cal.get(Calendar.WEEK_OF_YEAR);
                while(unit <= 1){ //test for previous year entries
                    day--;
                    cal.set(cal.get(Calendar.YEAR), 11, day);
                    unit = cal.get(Calendar.WEEK_OF_YEAR);
                }
            }
            else {
                unit -= 2;
            }
        }
        cal = Calendar.getInstance();
        unit = cal.get(Calendar.MONTH);
        for (int i = 0; i < 6; i++) {
            cal.set(Calendar.MONTH, unit);
            cal.set(Calendar.DAY_OF_MONTH, 1);
            dateMap.get(Interval.MONTH).add(0, dateString(cal.getTime()));
            if (unit < 0) {
                unit = cal.get(Calendar.MONTH);
            }
            unit--;
        }
        cal = Calendar.getInstance();
        unit = (cal.get(Calendar.MONTH) / 3) * 3;
        for (int i = 0; i < 6; i++) {
            cal.set(Calendar.MONTH, unit);
            cal.set(Calendar.DAY_OF_MONTH, 1);
            dateMap.get(Interval.QUARTER).add(0, dateString(cal.getTime()));
            if (unit < 0) {
                unit = 9;
            }
            unit -= 3;
        }
        cal = Calendar.getInstance();
        unit = cal.get(Calendar.YEAR);
        for (int i = 0; i < 6; i++) {
            cal.set(Calendar.YEAR, unit);
            cal.set(Calendar.DAY_OF_YEAR, 1);
            dateMap.get(Interval.YEAR).add(0, dateString(cal.getTime()));
            if (unit < 0) {
                unit = cal.get(Calendar.MONTH);
            }
            unit -= 1;
        }
        for (Interval interval : Interval.values()) {
            System.out.println(interval + "\t" + Joiner.on(", ").join(dateMap.get(interval)));
        }
        return dateMap;
    }

    /**
     * separate a value from an interval so that other interval types can use it
     * @param sourceInterval
     * @param targetInterval
     * @param amount
     * @return
     */
    public static BigDecimal getAmountForInterval(Interval sourceInterval, Interval targetInterval, BigDecimal amount) {
        BigDecimal result = null;
        switch (sourceInterval) {
            case WEEK:
                switch (targetInterval) {
                    case WEEK:
                        result = amount;
                        break;
                    case FORTNIGHT:
                        result = amount.multiply(BigDecimal.valueOf(2));
                        break;
                    case MONTH:
                        result = amount.multiply(BigDecimal.valueOf(52)).divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);
                        break;
                    case QUARTER:
                        result = amount.multiply(BigDecimal.valueOf(52)).divide(BigDecimal.valueOf(4), 2, RoundingMode.HALF_UP);
                        break;
                    case YEAR:
                        result = amount.multiply(BigDecimal.valueOf(52));
                        break;
                    default:
                        break;
                }
                break;
            case FORTNIGHT:
                switch (targetInterval) {
                    case WEEK:
                        result = amount.divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
                        break;
                    case FORTNIGHT:
                        result = amount;
                        break;
                    case MONTH:
                        result = amount.multiply(BigDecimal.valueOf(26)).divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);
                        break;
                    case QUARTER:
                        result = amount.multiply(BigDecimal.valueOf(26)).divide(BigDecimal.valueOf(4), 2, RoundingMode.HALF_UP);
                        break;
                    case YEAR:
                        result = amount.multiply(BigDecimal.valueOf(26));
                        break;
                    default:
                        break;
                }
                break;
            case MONTH:
                switch (targetInterval) {
                    case WEEK:
                        result = amount.multiply(BigDecimal.valueOf(12)).divide(BigDecimal.valueOf(52), 2, RoundingMode.HALF_UP);
                        break;
                    case FORTNIGHT:
                        result = amount.multiply(BigDecimal.valueOf(12)).divide(BigDecimal.valueOf(52), 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(2));
                        break;
                    case MONTH:
                        result = amount;
                        break;
                    case QUARTER:
                        result = amount.multiply(BigDecimal.valueOf(3));
                        break;
                    case YEAR:
                        result = amount.multiply(BigDecimal.valueOf(12));
                        break;
                    default:
                        break;
                }
                break;
            case QUARTER:
                switch (targetInterval) {
                    case WEEK:
                        result = amount.multiply(BigDecimal.valueOf(4)).divide(BigDecimal.valueOf(52), 2, RoundingMode.HALF_UP);
                        break;
                    case FORTNIGHT:
                        result = amount.multiply(BigDecimal.valueOf(4)).divide(BigDecimal.valueOf(52), 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(2));
                        break;
                    case MONTH:
                        result = amount.divide(BigDecimal.valueOf(3), 2, RoundingMode.HALF_UP);
                        break;
                    case QUARTER:
                        result = amount;
                        break;
                    case YEAR:
                        result = amount.multiply(BigDecimal.valueOf(4));
                        break;
                    default:
                        break;
                }
                break;
            case YEAR:
                switch (targetInterval) {
                    case WEEK:
                        result = amount.divide(BigDecimal.valueOf(52), 2, RoundingMode.HALF_UP);
                        break;
                    case FORTNIGHT:
                        result = amount.divide(BigDecimal.valueOf(52), 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(2));
                        break;
                    case MONTH:
                        result = amount.divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);
                        break;
                    case QUARTER:
                        result = amount.divide(BigDecimal.valueOf(3), 2, RoundingMode.HALF_UP);
                        break;
                    case YEAR:
                        result = amount;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
        return result;
    }

    /**
     * gets the value map for an interval from all interval sources
     * @param interval
     * @param amount
     * @return
     */
    public static Map<Interval, BigDecimal> getAmountMapForInterval(Interval interval, BigDecimal amount) {
        Map<Interval, BigDecimal> map = Maps.newHashMap();
        switch (interval) {
            case WEEK:
                map.put(Interval.WEEK, amount);
                map.put(Interval.FORTNIGHT, amount.multiply(BigDecimal.valueOf(2)));
                map.put(Interval.MONTH, amount.multiply(BigDecimal.valueOf(52)).divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP));
                map.put(Interval.QUARTER, amount.multiply(BigDecimal.valueOf(52)).divide(BigDecimal.valueOf(4), 2, RoundingMode.HALF_UP));
                map.put(Interval.YEAR, amount.multiply(BigDecimal.valueOf(52)));
                break;
            case FORTNIGHT:
                map.put(Interval.WEEK, amount.divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP));
                map.put(Interval.FORTNIGHT, amount);
                map.put(Interval.MONTH, amount.multiply(BigDecimal.valueOf(26)).divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP));
                map.put(Interval.QUARTER, amount.multiply(BigDecimal.valueOf(26)).divide(BigDecimal.valueOf(4), 2, RoundingMode.HALF_UP));
                map.put(Interval.YEAR, amount.multiply(BigDecimal.valueOf(26)));
                break;
            case MONTH:
                map.put(Interval.WEEK, amount.multiply(BigDecimal.valueOf(12)).divide(BigDecimal.valueOf(52), 2, RoundingMode.HALF_UP));
                map.put(Interval.FORTNIGHT, map.get(Interval.WEEK).multiply(BigDecimal.valueOf(2)));
                map.put(Interval.MONTH, amount);
                map.put(Interval.QUARTER, amount.multiply(BigDecimal.valueOf(3)));
                map.put(Interval.YEAR, amount.multiply(BigDecimal.valueOf(12)));
                break;
            case QUARTER:
                map.put(Interval.WEEK, amount.multiply(BigDecimal.valueOf(4)).divide(BigDecimal.valueOf(52), 2, RoundingMode.HALF_UP));
                map.put(Interval.FORTNIGHT, map.get(Interval.WEEK).multiply(BigDecimal.valueOf(2)));
                map.put(Interval.MONTH, amount.divide(BigDecimal.valueOf(3), 2, RoundingMode.HALF_UP));
                map.put(Interval.QUARTER, amount);
                map.put(Interval.YEAR, amount.multiply(BigDecimal.valueOf(4)));
                break;
            case YEAR:
                map.put(Interval.WEEK, amount.divide(BigDecimal.valueOf(52), 2, RoundingMode.HALF_UP));
                map.put(Interval.FORTNIGHT, map.get(Interval.WEEK).multiply(BigDecimal.valueOf(2)));
                map.put(Interval.MONTH, amount.divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP));
                map.put(Interval.QUARTER, amount.divide(BigDecimal.valueOf(3), 2, RoundingMode.HALF_UP));
                map.put(Interval.YEAR, amount);
                break;
            default:
        }
        return map;
    }
}
