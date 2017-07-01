package com.basicBudgetBuilder.repository;

import com.basicBudgetBuilder.domain.*;
import com.basicBudgetBuilder.representation.AutoDebitFullTextSearchRep;
import com.basicBudgetBuilder.representation.BudgetFullTextSearchRep;
import com.basicBudgetBuilder.representation.DebitFullTextSearchRep;
import com.basicBudgetBuilder.representation.FullTextSearchEntity;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for fulltext search related query entries
 * Created by Hanzi Jing on 9/04/2017.
 */
@Repository
public class FullTextSearchCustomerRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /** Lambda express implementation of row mapper interface */
    private RowMapper<FullTextSearchEntity> debitRowMapper = (rs, rowNumber)-> {
        double score = rs.getDouble("score");
        Category category = new Category(rs.getLong("category_id"),
                rs.getString("name"), rs.getString("colour"));
        Debit debit = new Debit(rs.getLong("id"),
                rs.getString("description"),
                rs.getBigDecimal("amount"),
                rs.getDate("date"),
                category);
        System.out.println();
        return new DebitFullTextSearchRep(score,debit.getId(), rs.getLong("user_id") , category.getId(), category.getName(),
                category.getColour(), debit.getDescription(), debit.getAmount(),debit.getDate().toString());
    };
    /** Search query for debit entries */
    private String debitSqlQuery = "SELECT MATCH(d.category_name, d.description) AGAINST (" +
            "CONCAT(\"\'\", ?  , \"\'\")" +
            " IN NATURAL LANGUAGE MODE) AS score, " +
            "d.category_id, category.colour, category.name, d.id, d.description, d.amount, d.date, d.user_id " +
            "FROM debit AS d " +
            "LEFT JOIN category ON d.category_id=category.id " +
            "WHERE d.user_id = ? " +
            "AND MATCH(d.category_name, d.description) AGAINST (" +
            "CONCAT(\"\'\", ?  , \"\'\")" +
             " IN NATURAL LANGUAGE MODE)";
    private RowMapper<FullTextSearchEntity> autoDebitRowMapper = (rs, rowNumber)-> {
        double score = rs.getDouble("score");
        Category category = new Category(rs.getLong("category_id"),
                rs.getString("name"), rs.getString("colour"));
        AutoDebit autoDebit = new AutoDebit(rs.getLong("id"),
                rs.getString("description"),
                rs.getBigDecimal("amount"),
                Interval.values()[rs.getInt("debit_interval")],
                category);
        System.out.println();
        return new AutoDebitFullTextSearchRep(
                score,
                autoDebit.getId(),
                rs.getLong("user_id") ,
                category.getId(),
                category.getName(),
                category.getColour(),
                autoDebit.getDescription(),
                autoDebit.getAmount(),
                autoDebit.getDebitInterval());
    };
    /** Search query for auto debit entries */
    private String autoDebitSqlQuery = "SELECT MATCH(d.category_name, d.description) AGAINST (" +
            "CONCAT(\"\'\", ?  , \"\'\")" +
            " IN NATURAL LANGUAGE MODE) AS score, " +
            "d.category_id, category.colour, category.name, d.id, d.description, d.amount, d.user_id, " +
            " d.debit_interval " +
            "FROM autodebit AS d " +
            "LEFT JOIN category ON d.category_id=category.id " +
            "WHERE d.user_id = ? " +
            "AND MATCH(d.category_name, d.description) AGAINST (" +
            "CONCAT(\"\'\", ?  , \"\'\")" +
            " IN NATURAL LANGUAGE MODE)";
    private RowMapper<FullTextSearchEntity> budgetRowMapper = (rs, rowNumber)-> {
        double score = rs.getDouble("score");
        Category category = new Category(rs.getLong("category_id"),
                rs.getString("name"), rs.getString("colour"));
        Budget budget = new Budget(rs.getLong("id"),
                rs.getString("description"),
                rs.getBigDecimal("amount"),
                Interval.values()[rs.getInt("budget_interval")],
                rs.getDate("effective_date"),
                category);
        System.out.println();
        return new BudgetFullTextSearchRep(score,budget.getId(),
                rs.getLong("user_id") ,
                category.getId(),
                category.getName(),
                category.getColour(),
                budget.getDescription(),
                budget.getAmount(),
                budget.getBudgetInterval(),
                budget.getEffectiveDate().toString());
    };
    /** Search query for budget entries */
    private String budgetSqlQuery = "SELECT MATCH(b.category_name, b.description) AGAINST (" +
            "CONCAT(\"\'\", ?  , \"\'\")" +
            " IN NATURAL LANGUAGE MODE) AS score, " +
            "b.category_id, category.colour, category.name, b.id, b.description, b.amount, b.effective_date, b.user_id, " +
            " b.budget_interval " +
            "FROM budget AS b " +
            "LEFT JOIN category ON b.category_id=category.id " +
            "WHERE b.user_id = ? " +
            "AND MATCH(b.category_name, b.description) AGAINST (" +
            "CONCAT(\"\'\", ?  , \"\'\")" +
            " IN NATURAL LANGUAGE MODE)";

    /** Search for all entries entries relevant to the query text */
    public List<FullTextSearchEntity> fullTextSearchDebit(long userId, String text){
        List<FullTextSearchEntity>result = Lists.newArrayList();
        result.addAll(jdbcTemplate.query(debitSqlQuery, debitRowMapper, text, userId, text));
        result.addAll(jdbcTemplate.query(autoDebitSqlQuery, autoDebitRowMapper, text, userId, text));
        result.addAll(jdbcTemplate.query(budgetSqlQuery, budgetRowMapper, text, userId, text));
        return result;
    }
}
