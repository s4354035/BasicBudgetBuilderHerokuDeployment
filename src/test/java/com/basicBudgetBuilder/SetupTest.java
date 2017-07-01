package com.basicBudgetBuilder;

import com.basicBudgetBuilder.domain.*;
import com.basicBudgetBuilder.repository.*;
import com.basicBudgetBuilder.representation.AutoDebitRep;
import com.basicBudgetBuilder.service.UserService;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Hanzi Jing on 9/04/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Service
public class SetupTest {
    @Autowired
    private UserService userService;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private BudgetRepository budgetRepository;
    @Autowired
    private DebitRepository debitRepository;
    @Autowired
    private BudgetCustomerRepository budgetCustomerRepository;
    @Autowired
    private AutoDebitRepository autoDebitRepository;

    private void createTestData(String email) {
        User user1 = new User(email, "1", Role.USER, "name");
        userService.save(user1);

        Category groceriesCategory = new Category("Groceries", "#00FF00", user1);
        categoryRepository.save(groceriesCategory);
        Category electronicsCategory = new Category("Electronics", "#FFFF00", user1);
        categoryRepository.save(electronicsCategory);
        Category gasCategory = new Category("Gas", "#FF8800", user1);
        categoryRepository.save(gasCategory);

        Budget groceriesBudget = new Budget("vacation",
                BigDecimal.valueOf(250.00),
                Interval.WEEK,
                Date.valueOf("2016-03-06"),
                user1,
                groceriesCategory);
        budgetRepository.save(groceriesBudget);

        Budget groceriesBudget1 = new Budget("regular",
                BigDecimal.valueOf(200.00),
                Interval.WEEK,
                Date.valueOf("2017-04-04"),
                user1,
                groceriesCategory);
        budgetRepository.save(groceriesBudget1);

        Budget groceriesBudget2 = new Budget("diet",
                BigDecimal.valueOf(180.00),
                Interval.WEEK,
                Date.valueOf("2017-05-01"),
                user1,
                groceriesCategory);
        budgetRepository.save(groceriesBudget2);

        Budget electronicsBudget = new Budget("mobile",
                BigDecimal.valueOf(400.00),
                Interval.MONTH,
                Date.valueOf("2015-03-06"),
                user1,
                electronicsCategory);
        budgetRepository.save(electronicsBudget);

        Budget electronicsBudget1 = new Budget("power fees",
                BigDecimal.valueOf(370.00),
                Interval.MONTH,
                Date.valueOf("2017-04-05"),
                user1,
                electronicsCategory);
        budgetRepository.save(electronicsBudget1);

        Budget electronicsBudget2 = new Budget("power fees",
                BigDecimal.valueOf(370.00),
                Interval.MONTH,
                Date.valueOf("2017-05-05"),
                user1,
                electronicsCategory);
        budgetRepository.save(electronicsBudget2);


        Budget gasBudget = new Budget("gas fees",
                BigDecimal.valueOf(450.00),
                Interval.MONTH,
                Date.valueOf("2015-03-06"),
                user1,
                gasCategory);
        budgetRepository.save(gasBudget);

        Budget gasBudget1 = new Budget("gas fees",
                BigDecimal.valueOf(500.00),
                Interval.FORTNIGHT,
                Date.valueOf("2016-04-05"),
                user1,
                gasCategory);
        budgetRepository.save(gasBudget1);

        Budget gasBudget2 = new Budget("gas fees",
                BigDecimal.valueOf(470.00),
                Interval.FORTNIGHT,
                Date.valueOf("2017-04-05"),
                user1,
                gasCategory);
        budgetRepository.save(gasBudget2);

        Debit groceriesDebit = new Debit("breakfast",
                BigDecimal.valueOf(9.90),
                Date.valueOf("2017-05-06"),
                user1,
                groceriesCategory,
                null);
        debitRepository.save(groceriesDebit);

        Debit groceriesDebit1 = new Debit("lunch",
                BigDecimal.valueOf(13.45),
                Date.valueOf("2017-05-04"),
                user1,
                groceriesCategory,
                null);
        debitRepository.save(groceriesDebit1);

        Debit groceriesDebit2 = new Debit("dinner",
                BigDecimal.valueOf(12.65),
                Date.valueOf("2017-05-04"),
                user1,
                groceriesCategory,
                null);
        debitRepository.save(groceriesDebit2);

        Debit groceriesDebit3 = new Debit("weekly groceries",
                BigDecimal.valueOf(256.43),
                Date.valueOf("2017-05-04"),
                user1,
                groceriesCategory,
                null);
        debitRepository.save(groceriesDebit3);

        Debit groceriesDebit4 = new Debit("weekly groceries",
                BigDecimal.valueOf(256.43),
                Date.valueOf("2017-04-29"),
                user1,
                groceriesCategory,
                null);
        debitRepository.save(groceriesDebit4);

        Debit groceriesDebit5 = new Debit("weekly groceries",
                BigDecimal.valueOf(242.58),
                Date.valueOf("2017-04-22"),
                user1,
                groceriesCategory,
                null);
        debitRepository.save(groceriesDebit5);

        Debit groceriesDebit6 = new Debit("weekly groceries",
                BigDecimal.valueOf(231.62),
                Date.valueOf("2017-03-22"),
                user1,
                groceriesCategory,
                null);
        debitRepository.save(groceriesDebit6);

        Debit groceriesDebit7 = new Debit("weekly groceries",
                BigDecimal.valueOf(216.25),
                Date.valueOf("2017-02-21"),
                user1,
                groceriesCategory,
                null);
        debitRepository.save(groceriesDebit7);

        Debit groceriesDebit8 = new Debit("weekly groceries",
                BigDecimal.valueOf(198.94),
                Date.valueOf("2017-02-14"),
                user1,
                groceriesCategory,
                null);
        debitRepository.save(groceriesDebit8);

        Debit groceriesDebit9 = new Debit("new years party",
                BigDecimal.valueOf(198.94),
                Date.valueOf("2017-01-01"),
                user1,
                groceriesCategory,
                null);
        debitRepository.save(groceriesDebit9);

        Debit groceriesDebit10 = new Debit("christmas party",
                BigDecimal.valueOf(198.94),
                Date.valueOf("2016-12-25"),
                user1,
                groceriesCategory,
                null);
        debitRepository.save(groceriesDebit10);

        Debit electronicsDebit = new Debit("monthly fees",
                BigDecimal.valueOf(347.35),
                Date.valueOf("2017-05-04"),
                user1,
                electronicsCategory,
                null);
        debitRepository.save(electronicsDebit);

        Debit electronicsDebit1 = new Debit("monthly fees",
                BigDecimal.valueOf(397.25),
                Date.valueOf("2017-04-04"),
                user1,
                electronicsCategory,
                null);
        debitRepository.save(electronicsDebit1);

        Debit electronicsDebit2 = new Debit("monthly fees",
                BigDecimal.valueOf(356.76),
                Date.valueOf("2017-03-04"),
                user1,
                electronicsCategory,
                null);
        debitRepository.save(electronicsDebit2);

        Debit gasDebit = new Debit("monthly fees",
                BigDecimal.valueOf(357.20),
                Date.valueOf("2017-05-04"),
                user1,
                gasCategory,
                null);
        debitRepository.save(gasDebit);

        Debit gasDebit1 = new Debit("monthly fees",
                BigDecimal.valueOf(314.18),
                Date.valueOf("2017-04-04"),
                user1,
                gasCategory,
                null);
        debitRepository.save(gasDebit1);

        Debit gasDebit2 = new Debit("monthly fees",
                BigDecimal.valueOf(426.88),
                Date.valueOf("2017-03-04"),
                user1,
                gasCategory,
                null);
        debitRepository.save(gasDebit2);

        AutoDebit autoDebit = new AutoDebit("monthly fees",
                BigDecimal.valueOf(347.35),
                Interval.MONTH,
                user1,
                electronicsCategory);
        autoDebitRepository.save(autoDebit);

        AutoDebit autoDebit1 = new AutoDebit("monthly fees",
                BigDecimal.valueOf(397.25),
                Interval.MONTH,
                user1,
                electronicsCategory);
        autoDebitRepository.save(autoDebit1);

        AutoDebit autoDebit2 = new AutoDebit("monthly fees",
                BigDecimal.valueOf(356.76),
                Interval.MONTH,
                user1,
                electronicsCategory);
        autoDebitRepository.save(autoDebit2);

        AutoDebit autoGatDebit = new AutoDebit("monthly fees",
                BigDecimal.valueOf(357.20),
                Interval.MONTH,
                user1,
                gasCategory);
        autoDebitRepository.save(autoGatDebit);

        AutoDebit autoGatDebit1 = new AutoDebit("monthly fees",
                BigDecimal.valueOf(314.18),
                Interval.MONTH,
                user1,
                gasCategory);
        autoDebitRepository.save(autoGatDebit1);

        AutoDebit autoGatDebit2 = new AutoDebit("monthly fees",
                BigDecimal.valueOf(426.88),
                Interval.MONTH,
                user1,
                gasCategory);
        autoDebitRepository.save(autoGatDebit2);
    }
    @Test
    public void Test(){
        createTestData("q");
        User user1 = new User("d", "1", Role.USER, "name");
        user1.setId((long)2);
        Category groceriesCategory = new Category("Groceries", "#00FF00", user1);
        Category electronicsCategory = new Category("Electronics", "#FFFF00", user1);
        groceriesCategory.setId((long)3);
        electronicsCategory.setId((long)4);
        List<Budget> budgets = budgetCustomerRepository.findAllByUserId((long)1);
        List<Debit> debits = debitRepository.findByUserAndCategoryIn(user1, Lists.newArrayList(groceriesCategory, electronicsCategory));
    }
}
