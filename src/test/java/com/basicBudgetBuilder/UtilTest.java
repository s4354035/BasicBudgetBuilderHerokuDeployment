package com.basicBudgetBuilder;

import com.basicBudgetBuilder.Utilities.DateUtil;
import com.basicBudgetBuilder.domain.Interval;
import org.junit.Test;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by Hanzi Jing on 9/05/2017.
 */
public class UtilTest {
    @Test
    public void test(){
        for (Interval interval : Interval.values()) {
            int[] days = DateUtil.getIntervalRestDays(interval);
            System.out.println(interval + ": " + days[0] + "  " + days[1]);
        }


        DateUtil.getSixFirstDateForInterval();
    }
}
