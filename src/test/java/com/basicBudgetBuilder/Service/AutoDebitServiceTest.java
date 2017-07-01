package com.basicBudgetBuilder.Service;

import com.basicBudgetBuilder.domain.Category;
import com.basicBudgetBuilder.domain.Interval;
import com.basicBudgetBuilder.domain.Role;
import com.basicBudgetBuilder.domain.User;
import com.basicBudgetBuilder.exceptions.BasicBudgetBuilderException;
import com.basicBudgetBuilder.repository.AutoDebitRepository;
import com.basicBudgetBuilder.repository.CategoryRepository;
import com.basicBudgetBuilder.representation.AutoDebitRep;
import com.basicBudgetBuilder.representation.BudgetRep;
import com.basicBudgetBuilder.service.AutoDebitService;
import com.basicBudgetBuilder.service.UserService;
import org.assertj.core.util.Lists;
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
import java.util.stream.Collectors;

/**
 * Created by Hanzi Jing on 15/+04/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Service
public class AutoDebitServiceTest {
    @Autowired
    private UserService userService;

    @Autowired
    private AutoDebitService autoDebitService;

    @Autowired
    private AutoDebitRepository autoDebitRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private User user = null;
    private Category category0 = null;
    private Category category1 = null;
    private Category category2 = null;
    private Category category3 = null;
    private Category category4 = null;
    private Category category5 = null;

    @Before
    public void setup()throws Exception {
        String email = "email";
        String password = "password";
        String name = "name";
        user = userService.findByEmail(email);
        if(user == null) {
            user = new User(email, password, Role.USER, name);
            userService.save(user);
            category0 = new Category("Green", "#00FF00", user);
            category1 = new Category("Red", "#FF0000", user);
            category2 = new Category("White", "#fCFfFc", user);
            category3 = new Category("Blue", "#0000FF", user);
            category4 = new Category("Gold", "#FFFF11", user);
            category5 = new Category("Magenta", "#FF01FF", user);
            categoryRepository.save(category0);
            categoryRepository.save(category1);
            categoryRepository.save(category2);
            categoryRepository.save(category3);
            categoryRepository.save(category4);
            categoryRepository.save(category5);
        }
        else{
            category0 = new Category("Green", "#00FF00", user);
            category1 = new Category("Red", "#FF0000", user);
            category2 = new Category("White", "#fCFfFc", user);
            category3 = new Category("Blue", "#0000FF", user);
            category4 = new Category("Gold", "#FFFF11", user);
            category5 = new Category("Magenta", "#FF01FF", user);
            category0 = categoryRepository.findByNameAndUser(category0.getName(), user);
            category1 = categoryRepository.findByNameAndUser(category1.getName(), user);
            category2 = categoryRepository.findByNameAndUser(category2.getName(), user);
            category3 = categoryRepository.findByNameAndUser(category3.getName(), user);
            category4 = categoryRepository.findByNameAndUser(category4.getName(), user);
            category5 = categoryRepository.findByNameAndUser(category5.getName(), user);
        }
    }
    @Test
    public void addTest() throws BasicBudgetBuilderException {
        //normal create AutoDebit scenario
        AutoDebitRep autoDebitRep = new AutoDebitRep("Green", "#00FF00", "It's Green",
                BigDecimal.valueOf(141.2), Interval.MONTH);
        autoDebitRep = autoDebitService.create(autoDebitRep, user);
        Assert.assertTrue(autoDebitRep.getId() > 0);
        Assert.assertTrue(autoDebitRep.getCategoryId() > 0);
        Assert.assertTrue(autoDebitRep.getUserId() > 0);

        //Successful no description Scenario
        AutoDebitRep autoDebitRep1 = new AutoDebitRep("Red", "#FF0000", null,
                BigDecimal.valueOf(1830.7), Interval.MONTH);
        autoDebitService.create(autoDebitRep1, user);
        Assert.assertTrue(autoDebitRep1.getId() > 0);
        Assert.assertTrue(autoDebitRep1.getCategoryId() > 0);
        Assert.assertTrue(autoDebitRep1.getUserId() > 0);


    }
//    @Test(expected=BasicBudgetBuilderException.class)
//    public void addTestExceptions()throws BasicBudgetBuilderException {
//
//        AutoDebitRep autoDebitRep = new AutoDebitRep("White", "#fCFfFc", "It's White",
//                null, Interval.MONTH, 1, "2017-12-14");
//        autoDebitService.create(autoDebitRep, user);
//    }
//
//    @Test
//    public void editTest() throws BasicBudgetBuilderException {
//        // Add a group of AutoDebit entries
//        AutoDebitRep autoDebitRep = new AutoDebitRep("Blue", "#0000FF", "It's Blue",
//                BigDecimal.valueOf(412.22), Interval.MONTH, 1, "2017-11-14");
//        autoDebitService.create(autoDebitRep, user);
//
//        // successful edit of an entry
//        AutoDebitRep autoDebitRep1 = new AutoDebitRep("Gold", "#FFFF11", "It's not Yellow",
//                BigDecimal.valueOf(962.30), Interval.MONTH, 1, "2017-10-12");
//        autoDebitRep1.setId(autoDebitRep.getId());
//        autoDebitService.edit(autoDebitRep1, user);
//
//    }
//    @Test(expected=BasicBudgetBuilderException.class)
//    public void editTestExceptions()throws BasicBudgetBuilderException {
//        // Add a AutoDebit entry
//        AutoDebitRep autoDebitRep = new AutoDebitRep("Magenta", "#FF01FF", "It's Magenta",
//                BigDecimal.valueOf(1275.34), Interval.MONTH, 1, "2018-08-14");
//        autoDebitRep = autoDebitService.create(autoDebitRep, user);
//
//        //Null entries scenarios
//        AutoDebitRep autoDebitRep1 = new AutoDebitRep("White", "#fCefFc", "It's White",
//                BigDecimal.valueOf(0), Interval.MONTH, 1, "2017-12-14");
//        autoDebitRep1.setId(autoDebitRep.getId());
//        autoDebitService.edit(autoDebitRep1, user);
//    }

    @Test
    public void getAllTest() throws BasicBudgetBuilderException {
        List<Category>categories = Lists.newArrayList(category0, category1, category2, category3, category4, category5);

        List<AutoDebitRep> autoDebitReps= autoDebitService.getAllForCategories(user,
                categories.stream().map(a->{
                    BudgetRep rep = new BudgetRep();
                    rep.setCategoryId(a.getId());
                    return rep;
                }).collect(Collectors.toList()));

        Assert.assertEquals(2,autoDebitReps.size());
    }

//    @Test
//    public void deleteTest() throws BasicBudgetBuilderException {
//        AutoDebitRep autoDebitRep = new AutoDebitRep("Green", "#00FF00", "It's Green",
//                BigDecimal.valueOf(141.2), Interval.MONTH, 1, "2017-12-14");
//        autoDebitRep = autoDebitService.create(autoDebitRep, user);
//        Assert.assertTrue(autoDebitService.delete(autoDebitRep));
//        Assert.assertNull(autoDebitRepository.findById(autoDebitRep.getId()));
//    }
}