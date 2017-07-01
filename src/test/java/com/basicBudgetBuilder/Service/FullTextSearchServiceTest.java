package com.basicBudgetBuilder.Service;

import com.basicBudgetBuilder.domain.Category;
import com.basicBudgetBuilder.domain.Role;
import com.basicBudgetBuilder.domain.User;
import com.basicBudgetBuilder.exceptions.BasicBudgetBuilderException;
import com.basicBudgetBuilder.repository.CategoryRepository;
import com.basicBudgetBuilder.representation.AutoDebitRep;
import com.basicBudgetBuilder.representation.BudgetRep;
import com.basicBudgetBuilder.representation.DebitRep;
import com.basicBudgetBuilder.representation.FullTextSearchEntity;
import com.basicBudgetBuilder.service.StatisticsService;

import com.basicBudgetBuilder.service.UserService;

import com.basicBudgetBuilder.service.BudgetService;
import com.basicBudgetBuilder.service.DebitService;
import  com.basicBudgetBuilder.service.AutoDebitService;
import com.basicBudgetBuilder.service.FullTextSearchService;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.List;

import static com.basicBudgetBuilder.domain.Interval.*;

/**
 * Created by Hanzi Jing on 15/+04/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Service
public class FullTextSearchServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BudgetService budgetService;

    @Autowired
    private DebitService debitService;

    @Autowired
    private AutoDebitService autoDebitService;



    @Autowired
    private FullTextSearchService searchService;


    @Autowired
    private StatisticsService statisticsService;

    private User user = null;
    BudgetRep budgetRep;
    BudgetRep budgetRep0;
    @Before
    public void setup()throws Exception {

        String email = "email";
        String password = "password";
        String name = "name";
        user = userService.findByEmail(email);
        if(user == null) {
            user = new User(email, password, Role.USER, name);
            userService.save(user);
            Category category = new Category("Uncategorized", "#555555", user);
            Category category0 = new Category("Green", "#00FF00", user);
            Category category1 = new Category("Red pen", "#FF0000", user);
            Category category2 = new Category("White", "#fCFfFc", user);
            Category category3 = new Category("Blue", "#0000FF", user);
            Category category4 = new Category("Gold", "#FFFF11", user);
            Category category5 = new Category("Magenta", "#FF01FF", user);
            categoryRepository.save(category);
            categoryRepository.save(category0);
            categoryRepository.save(category1);
            categoryRepository.save(category2);
            categoryRepository.save(category3);
            categoryRepository.save(category4);
            categoryRepository.save(category5);



            //normal create Debit scenario
            DebitRep debitRep = new DebitRep("Green", "#00FF00", "book notes pen ink gas",
                    BigDecimal.valueOf(141.2), "2017-4-14");
            DebitRep debitRep1 = new DebitRep("Green", "#00FF00", "book notes",
                    BigDecimal.valueOf(1830.7), "2017-5-4");
            DebitRep debitRep2 = new DebitRep("Red", "#FF0000", "book",
                    BigDecimal.valueOf(1830.7), null);
            DebitRep debitRep3 = new DebitRep("Magenta", "#00FFf0", "speedy",
                    BigDecimal.valueOf(130.7), "2017-5-4");
            debitService.create(debitRep, user);
            debitService.create(debitRep1, user);
            debitService.create(debitRep2, user);
            debitService.create(debitRep3, user);

            AutoDebitRep autoDebitRep = new AutoDebitRep("Green", "#00FF00", "book pen gas",
                    BigDecimal.valueOf(141.2), MONTH);
            AutoDebitRep autoDebitRep1 = new AutoDebitRep("Red pen", "#FF0000", "book",
                    BigDecimal.valueOf(1830.7), MONTH);

            autoDebitService.create(autoDebitRep, user);
            autoDebitService.create(autoDebitRep1, user);

            budgetRep = new BudgetRep("Green", "#00FF00", "notes, pen",
                    BigDecimal.valueOf(141.2), WEEK, "2017-12-14");
            //Successful overlay of old budget scenario
            budgetRep0 = new BudgetRep("Magenta", "#00FFf0", "book notes pen ink",
                    BigDecimal.valueOf(371.00), FORTNIGHT,  "2017-12-24");
//            //Category name clash Ignored Entry Scenario
//            BudgetRep budgetRep1 = new BudgetRep("Green", "#0000FF", "It's Blue",
//                    BigDecimal.valueOf(630.7), MONTH, false, false, "2017-11-23");

            budgetService.create(budgetRep, user);
            budgetService.create(budgetRep0, user);
//            budgetService.create(budgetRep1, user);
        }
    }
    @Test
    public void test() throws BasicBudgetBuilderException {
        List<FullTextSearchEntity> searchRescult = searchService.fullTextSearch(user, "book notes pen ink gas");
        Assert.assertEquals(searchRescult.size(), 7);
    }
    @Test
    public void testStat()throws BasicBudgetBuilderException{
//        statisticsService.getStats(user, Lists.newArrayList(budgetRep, budgetRep0));
        statisticsService.getStats(user, Lists.newArrayList(budgetRep0, budgetRep));
    }

}