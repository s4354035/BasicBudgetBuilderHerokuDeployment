package com.basicBudgetBuilder.Service;

import com.basicBudgetBuilder.domain.Category;
import com.basicBudgetBuilder.domain.Role;
import com.basicBudgetBuilder.domain.User;
import com.basicBudgetBuilder.exceptions.BasicBudgetBuilderException;
import com.basicBudgetBuilder.repository.CategoryRepository;
import com.basicBudgetBuilder.repository.DebitRepository;
import com.basicBudgetBuilder.representation.DebitRep;
import com.basicBudgetBuilder.service.DebitService;
import com.basicBudgetBuilder.service.UserService;
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

/**
 * Created by Hanzi Jing on 15/+04/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Service
public class DebitServiceTest {
    @Autowired
    private UserService userService;

    @Autowired
    private DebitService debitService;

    @Autowired
    private DebitRepository debitRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private User user = null;

    @Before
    public void setup()throws Exception {

        String email = "email";
        String password = "password";
        String name = "name";
        user = userService.findByEmail(email);
        if(user == null) {
            user = new User(email, password, Role.USER, name);
            userService.save(user);
//            Category category = new Category("Uncategorized", "#555555", user);
            Category category0 = new Category("Green", "#00FF00", user);
            Category category1 = new Category("Red", "#FF0000", user);
            Category category2 = new Category("White", "#fCFfFc", user);
            Category category3 = new Category("Blue", "#0000FF", user);
            Category category4 = new Category("Gold", "#FFFF11", user);
            Category category5 = new Category("Magenta", "#FF01FF", user);
//            categoryRepository.save(category);
            categoryRepository.save(category0);
            categoryRepository.save(category1);
            categoryRepository.save(category2);
            categoryRepository.save(category3);
            categoryRepository.save(category4);
            categoryRepository.save(category5);
        }

    }
    @Test
    public void addTest() throws BasicBudgetBuilderException {
        //normal create Debit scenario
        DebitRep debitRep = new DebitRep("Green", "#00FF00", "It's Green",
                BigDecimal.valueOf(141.2), "2017-12-14");
        debitRep = debitService.create(debitRep, user);
        Assert.assertTrue(debitRep.getId() > 0);
        Assert.assertTrue(debitRep.getCategoryId() > 0);
        Assert.assertTrue(debitRep.getUserId() > 0);

        //Successful no description Scenario
        DebitRep debitRep1 = new DebitRep("Red", "#FF0000", null,
                BigDecimal.valueOf(1830.7), "2017-11-23");
        debitService.create(debitRep1, user);
        Assert.assertTrue(debitRep1.getId() > 0);
        Assert.assertTrue(debitRep1.getCategoryId() > 0);
        Assert.assertTrue(debitRep1.getUserId() > 0);

        //Successful no date scenario
        DebitRep debitRep2 = new DebitRep("Red", "#FF0000", "dateless",
                BigDecimal.valueOf(1830.7), null);
        debitService.create(debitRep2, user);
        Assert.assertTrue(debitRep2.getId() > 0);
        Assert.assertTrue(debitRep2.getCategoryId() > 0);
        Assert.assertTrue(debitRep2.getUserId() > 0);

        //Successful no category scenario
        DebitRep debitRep3 = new DebitRep(null, null, "speedy",
                BigDecimal.valueOf(1830.7), "2017-11-23");
        debitService.create(debitRep3, user);
        Assert.assertTrue(debitRep3.getId() > 0);
        Assert.assertTrue(debitRep3.getCategoryId() > 0);
        Assert.assertTrue(debitRep3.getUserId() > 0);

    }
    @Test(expected=BasicBudgetBuilderException.class)
    public void addTestExceptions()throws BasicBudgetBuilderException {

        DebitRep debitRep = new DebitRep("White", "#fCFfFc", "It's White",
                null, "2017-12-14");
        debitService.create(debitRep, user);
    }

    @Test
    public void editTest() throws BasicBudgetBuilderException {
        // Add a group of Debit entries
        DebitRep debitRep = new DebitRep("Blue", "#0000FF", "It's Blue",
                BigDecimal.valueOf(412.22), "2017-11-14");
        debitService.create(debitRep, user);

        // successful edit of an entry
        DebitRep debitRep1 = new DebitRep("Gold", "#FFFF11", "It's not Yellow",
                BigDecimal.valueOf(962.30), "2017-10-12");
        debitRep1.setId(debitRep.getId());
        debitService.edit(debitRep1, user);

    }
    @Test(expected=BasicBudgetBuilderException.class)
    public void editTestExceptions()throws BasicBudgetBuilderException {
        // Add a group of Debit entries
        DebitRep debitRep = new DebitRep("Magenta", "#FF01FF", "It's Magenta",
                BigDecimal.valueOf(1275.34), "2018-08-14");
        debitRep = debitService.create(debitRep, user);

        //Null entries scenarios
        DebitRep debitRep1 = new DebitRep("White", "#fCefFc", "It's White",
                BigDecimal.valueOf(0), "2017-12-14");
        debitRep1.setId(debitRep.getId());
        debitService.edit(debitRep1, user);
    }

    @Test
    public void getAllTest() throws BasicBudgetBuilderException {
        List<DebitRep> debitReps= debitService.getByCategories(user, null);
        Assert.assertEquals(5, debitReps.size());
    }

    @Test
    public void deleteTest() throws BasicBudgetBuilderException {
        DebitRep debitRep = new DebitRep("Green", "#00FF00", "It's Green",
                BigDecimal.valueOf(141.2), "2017-12-14");
        debitRep = debitService.create(debitRep, user);
        Assert.assertTrue(debitService.delete(debitRep.getId()));
        Assert.assertNull(debitRepository.findById(debitRep.getId()));
    }
}