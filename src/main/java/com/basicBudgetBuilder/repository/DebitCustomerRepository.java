package com.basicBudgetBuilder.repository;

import com.basicBudgetBuilder.Utilities.DateUtil;
import com.basicBudgetBuilder.domain.Category;
import com.basicBudgetBuilder.domain.Debit;
import com.basicBudgetBuilder.domain.Interval;
import com.basicBudgetBuilder.representation.BudgetDebitRep;
import com.basicBudgetBuilder.representation.CategoryRep;
import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * Repository for Non-Standard debit related query entries
 * Created by Hanzi Jing on 9/04/2017.
 */
@Repository
public class DebitCustomerRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private NamedParameterJdbcTemplate namedJdbcTemplate;

    /** Lambda express implementation of row mapper interface */
    private RowMapper<Debit> rowMapper = (rs, rowNumber)-> {
        Category category = new Category(rs.getLong("category_id"),
                rs.getString("name"), rs.getString("colour"));
        return new Debit(rs.getLong("id"),
                rs.getString("description"),
                rs.getBigDecimal("amount"),
                rs.getDate("date"),
                category);
    };
    private String sqlQuery =
            "SELECT d.category_id, category.colour, category.name, d.id, d.description, d.amount, d.date\n" +
                    "FROM debit AS d\n" +
                    "LEFT JOIN category ON d.category_id=category.id\n" +
                    "LEFT JOIN autodebit ON d.autodebit_id=autodebit.id\n" +
                    "WHERE d.user_id = ? AND d.category_id = ?";

    private String sqlQueryForCategories =
            "SELECT d.category_id, category.colour, category.name, d.id, d.description, d.amount, d.date\n" +
                    "FROM debit AS d\n" +
                    "LEFT JOIN category ON d.category_id=category.id\n" +
                    "LEFT JOIN autodebit ON d.autodebit_id=autodebit.id\n" +
                    "WHERE d.user_id = :uid AND d.category_id in ( :ids )";
    
    /** Find all Debit entries that have the query user and category */
    public List<Debit> findDebitByCategory(long userId, long categoryId){
        return  jdbcTemplate.query(sqlQuery, rowMapper, userId, categoryId);
    }

    /** Find all Debit entries that have the query user and categories */
    public List<Debit> findDebitByCategories(long userId, List<Long> categoryIds){
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("ids", categoryIds);
        parameters.addValue("uid", userId);
        return namedJdbcTemplate.query(sqlQueryForCategories, parameters, rowMapper);
    }

    /** Find all Debit entries that have the query user and category within the query interval */
    public List<Debit> findDebitByCategoryAndInterval(long userId, long categoryId, Interval interval){
        String finalQuery = sqlQuery;
        if(interval == Interval.WEEK){
            finalQuery += " AND yearweek(d.date) = yearweek(CURRENT_TIMESTAMP)";
        }
        if(interval == Interval.FORTNIGHT){
            finalQuery += " AND ceil(yearweek(d.date)/2) = ceil(yearweek(CURRENT_TIMESTAMP)/2)";
        }
        if(interval == Interval.MONTH){
            finalQuery += " AND Year(d.date) = Year(CURRENT_TIMESTAMP) AND Month(d.date) = Month(CURRENT_TIMESTAMP)";
        }
        if(interval == Interval.QUARTER){
            finalQuery += " AND Year(d.date) = Year(CURRENT_TIMESTAMP) AND Quarter(d.date) = Quarter(CURRENT_TIMESTAMP)";
        }
        if(interval == Interval.YEAR){
            finalQuery += " AND Year(d.date) = Year(CURRENT_TIMESTAMP)";
        }
        return  jdbcTemplate.query(finalQuery, rowMapper, userId, categoryId);
    }

    /** Find the sum of all Debit entry amounts that have the query user within the query interval */
    public BigDecimal getSumForInterval(final long userId, Interval interval){
        BigDecimal sum = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
        String conditionString = DateUtil.getIntervalStartDate(interval);
        RowMapper<BigDecimal> sumMapper  = (rs, rowNumber)->{
            BigDecimal sum1  = rs.getBigDecimal("sum");
            return sum1 != null ? sum1 : BigDecimal.ZERO;
        };
        String sql = "SELECT SUM(amount) AS sum FROM debit  " +
                "WHERE user_id = ? AND date <= CURRENT_TIMESTAMP AND date >= \""+ conditionString + "\"";
        return jdbcTemplate.query(sql, sumMapper, userId).get(0).setScale(2, RoundingMode.HALF_UP);
    }
    /** Find the sum of all Debit entry amounts that have the query user and category 
     *  within six units of the query interval */
    public void getSixIntervalSumByCategoryIdsAndInterval(final long userId, final List<Long>categoryIds,
                                                          Interval interval,
                                                          final Map<CategoryRep, Map<String, BudgetDebitRep>>map){
        String argString = "";
        String conditionString = "";
        switch (interval){
            case WEEK:
                conditionString = "6 WEEK";
                argString = "week(d.date, 6) AS interval_number, DATE_SUB(d.date, INTERVAL WEEKDAY(d.date) + 1 DAY) AS start_date";
                break;
            case FORTNIGHT:
                int week = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);
                conditionString = week % 2 == 0 ? "12 WEEK" : "11 WEEK";
                argString = "floor(week(d.date)/2) AS interval_number, DATE_SUB(DATE_ADD(MAKEDATE(Year(d.date), 1), " +
                        "INTERVAL floor(week(d.date)/2)*2 -1 WEEK), INTERVAL (WEEKDAY(DATE_ADD(MAKEDATE(Year(d.date), 1), " +
                        "INTERVAL floor(week(d.date)/2)*2 -1 WEEK)) + 1) DAY) As start_date";
                argString = "week(d.date, 6) AS interval_number, DATE_SUB(d.date, INTERVAL WEEKDAY(d.date) + 1 + (1- (week(d.date, 6)%2))*7 DAY) AS start_date";
                break;
            case MONTH:
                conditionString = "6 MONTH";
                argString = "month(d.date) AS interval_number, ADDDATE(LAST_DAY(SUBDATE(d.date, INTERVAL 1 MONTH)),1) As start_date";
                break;
            case QUARTER:
                conditionString = "6 QUARTER";
                argString = "Quarter(d.date) AS interval_number, d.date, DATE_ADD(MAKEDATE(Year(d.date), 1), " +
                        "INTERVAL QUARTER(d.date) - 1 QUARTER) As start_date";
                break;
            case YEAR:
                conditionString = "6 YEAR";
                argString = "Year(d.date) AS interval_number, MAKEDATE(year(d.date),1) As start_date";
                break;
            default:
                break;
        }
        if(argString.isEmpty()){
            return ;
        }
        RowMapper<BigDecimal> categorySumMapper = (rs, rowNumber)-> {
            String categoryName = rs.getString("name");
            String categoryColour = rs.getString("color");
            long categoryId = rs.getLong("category_id");
            CategoryRep categoryRep = new CategoryRep(categoryId, categoryName, categoryColour);
            BigDecimal sum = rs.getBigDecimal("category_sum");
            String date = DateUtil.dateString(rs.getDate("start_date"));
            if( map.get(categoryRep).containsKey(date)) {
                map.get(categoryRep).get(date).setDebit(map.get(categoryRep).get(date).getDebit().add(sum));
            }
            return sum;
        };
        String sql = "SELECT category.id as category_id, category.colour as color, category.name as name, SUM(d.amount) AS category_sum," + argString + " "+
                "FROM debit AS d " +
                "LEFT JOIN category ON d.category_id = category.id "+
                "WHERE d.user_id = :uid AND d.category_id in ( :ids ) AND d.date <= CURRENT_TIMESTAMP AND d.date > DATE_SUB(CURRENT_TIMESTAMP, INTERVAL " +
                conditionString + ") "+
                "GROUP BY d.category_id, Year(d.date), interval_number "+
                "ORDER BY d.category_id, d.date";
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("ids", categoryIds);
        parameters.addValue("uid", userId);
        namedJdbcTemplate.query(sql, parameters, categorySumMapper);
    }
}
