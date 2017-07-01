package com.basicBudgetBuilder.Service;

import com.basicBudgetBuilder.domain.Interval;
import com.basicBudgetBuilder.domain.Role;
import com.basicBudgetBuilder.domain.User;
import com.basicBudgetBuilder.exceptions.BasicBudgetBuilderException;
import com.basicBudgetBuilder.repository.BudgetRepository;
import com.basicBudgetBuilder.representation.BudgetRep;
import com.basicBudgetBuilder.service.BudgetService;
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
public class BudgetServiceTest {
    @Autowired
    private UserService userService;

    @Autowired
    private BudgetService budgetService;

    @Autowired
    private BudgetRepository budgetRepository;


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
        }
    }
    @Test
    public void addTest() throws BasicBudgetBuilderException {
        //normal create budget scenario
        BudgetRep budgetRep = new BudgetRep("Green", "#00FF00", "It's Green",
                BigDecimal.valueOf(141.2), Interval.WEEK,  "2017-12-14");
        budgetRep = budgetService.create(budgetRep, user);
        Assert.assertTrue(budgetRep.getId() > 0);
        Assert.assertTrue(budgetRep.getCategoryId() > 0);
        Assert.assertTrue(budgetRep.getUserId() > 0);
        //Successful overlay of old budget scenario
        BudgetRep budgetRep0 = new BudgetRep("Green", "#00FF00", "It's Still Green",
                BigDecimal.valueOf(371.00), Interval.FORTNIGHT,  "2017-12-24");
        budgetRep0 = budgetService.create(budgetRep0, user);
        Assert.assertTrue(budgetRep0.getId() > 0);
        Assert.assertTrue(budgetRep0.getCategoryId() > 0);
        Assert.assertTrue(budgetRep0.getUserId() > 0);

        //Category name clash Ignored Entry Scenario
        BudgetRep budgetRep1 = new BudgetRep("Green", "#0000FF", "It's Blue",
                BigDecimal.valueOf(630.7), Interval.MONTH,"2017-11-23");
        budgetService.create(budgetRep1, user);
        Assert.assertTrue(budgetRep0.getId() > 0);
        Assert.assertTrue(budgetRep0.getCategoryId() > 0);
        Assert.assertTrue(budgetRep0.getUserId() > 0);

        //Successful no description Scenario
        BudgetRep budgetRep2 = new BudgetRep("Red", "#FF0000", null,
                BigDecimal.valueOf(1830.7), Interval.QUARTER, "2017-11-23");
        budgetService.create(budgetRep2, user);
        Assert.assertTrue(budgetRep0.getId() > 0);
        Assert.assertTrue(budgetRep0.getCategoryId() > 0);
        Assert.assertTrue(budgetRep0.getUserId() > 0);

    }
    @Test(expected=BasicBudgetBuilderException.class)
    public void addTestExceptions()throws BasicBudgetBuilderException {

        //Category colour clash Scenario
        BudgetRep budgetRep = new BudgetRep("Blue", "#00FF00", "It's Blue",
                BigDecimal.valueOf(111.11), Interval.FORTNIGHT, "2015-02-11");
        budgetService.create(budgetRep, user);

        //Duplicated name and date Scenario
        BudgetRep budgetRep0 = new BudgetRep("Green", "#00FF01", "It's also Green",
                BigDecimal.valueOf(111.11), Interval.WEEK,  "2017-12-14");
        budgetService.create(budgetRep0, user);

        //Null entries scenarios
        BudgetRep budgetRep1 = new BudgetRep(null, "#0201A9", "It's Blue",
                BigDecimal.valueOf(111.11), Interval.MONTH, "2017-12-14");
        budgetService.create(budgetRep1, user);

        BudgetRep budgetRep2 = new BudgetRep("Magenta", null, "It's Magenta",
                BigDecimal.valueOf(111.11), Interval.QUARTER,  "2017-12-14");
        budgetService.create(budgetRep2, user);

        BudgetRep budgetRep3 = new BudgetRep("White", "#fCFfFc", "It's White",
                null, Interval.YEAR,  "2017-12-14");
        budgetService.create(budgetRep3, user);

        BudgetRep budgetRep4 = new BudgetRep("Black", "#030504", "It's Black",
                BigDecimal.valueOf(111.11), null, "2017-12-14");
        budgetService.create(budgetRep4, user);

        BudgetRep budgetRep5 = new BudgetRep("Cyan", "#00FEfe", "It's Cyan",
                BigDecimal.valueOf(111.11), Interval.WEEK,  null);
        budgetService.create(budgetRep5, user);

        BudgetRep budgetRep6 = new BudgetRep("Yellow", "#56789a", "It's Yellow",
                BigDecimal.valueOf(111.11), Interval.YEAR,  "2017-12-14");
        budgetService.create(budgetRep6, null);

        budgetService.create(null, user);

        //Invalid Date
        BudgetRep budgetRep8 = new BudgetRep("Grey", "#555555", "It's Grey",
                BigDecimal.valueOf(111.11), Interval.YEAR, "2017-15-14");
        budgetService.create(budgetRep8, user);

        //Invalid Colour
        BudgetRep budgetRep9 = new BudgetRep("Unknown", "#FFFG00", "It's Unknown",
                BigDecimal.valueOf(111.11), Interval.YEAR,  "2017-12-14");
        budgetService.create(budgetRep9, user);
    }

    @Test
    public void editTest() throws BasicBudgetBuilderException {
        // Add a group of Budget entries
        BudgetRep budgetRep = new BudgetRep("Blue", "#0000FF", "It's Blue",
                BigDecimal.valueOf(412.22), Interval.MONTH,  "2017-11-14");
        budgetService.create(budgetRep, user);

        BudgetRep budgetRep1 = new BudgetRep("Cyan", "#00FFFF", "It's Cyan",
                BigDecimal.valueOf(45.72), Interval.WEEK,  "2012-12-14");
        budgetRep1 = budgetService.create(budgetRep1, user);

        BudgetRep budgetRep2 = new BudgetRep("Yellow", "#FFFF00", "It's Yellow",
                BigDecimal.valueOf(27146.5), Interval.YEAR,  "2017-12-14");
        budgetRep2 = budgetService.create(budgetRep2, user);

        // successful edit of an entry
        BudgetRep budgetRep3 = new BudgetRep("Gold", "#FFFF11", "It's not Yellow",
                BigDecimal.valueOf(962.30), Interval.MONTH,  "2017-10-12");
        budgetRep3.setId(budgetRep2.getId());
        budgetService.edit(budgetRep3, user);

        BudgetRep budgetRep4 = new BudgetRep("Cyan", "#00FFFF", "It's still Cyan",
                BigDecimal.valueOf(22.32), Interval.WEEK,  "2012-12-14");
        budgetRep4.setId(budgetRep1.getId());
        budgetService.edit(budgetRep4, user);
    }
    @Test(expected=BasicBudgetBuilderException.class)
    public void editTestExceptions()throws BasicBudgetBuilderException {
        // Add a group of Budget entries
        BudgetRep budgetRep = new BudgetRep("Magenta", "#FF01FF", "It's Magenta",
                BigDecimal.valueOf(1275.34), Interval.QUARTER, "2018-08-14");
        budgetRep = budgetService.create(budgetRep, user);

        BudgetRep budgetRep0 = new BudgetRep("White", "#FFFFFF", "It's White",
                BigDecimal.valueOf(7415.62), Interval.YEAR, "2015-04-14");
        budgetRep0 = budgetService.create(budgetRep0, user);

        BudgetRep budgetRep1 = new BudgetRep("Black", "#000000", "It's Black",
                BigDecimal.valueOf(84.47), Interval.WEEK,  "2016-01-14");
        budgetRep1 = budgetService.create(budgetRep1, user);

        //Null entries scenarios
        BudgetRep budgetRep2 = new BudgetRep(null, "#0211A9", "It's Blue",
                BigDecimal.valueOf(111.11), Interval.MONTH,  "2017-12-14");
        BudgetRep budgetRep3 = new BudgetRep("Magenta", null, "It's Magenta",
                BigDecimal.valueOf(111.11), Interval.QUARTER,  "2017-12-14");
        BudgetRep budgetRep4 = new BudgetRep("White", "#fCefFc", "It's White",
                null, Interval.YEAR,  "2017-12-14");
        BudgetRep budgetRep5 = new BudgetRep("Black", "#031504", "It's Black",
                BigDecimal.valueOf(111.11), null,  "2017-12-14");
        BudgetRep budgetRep6 = new BudgetRep("Cyan", "#01FEfe", "It's Cyan",
                BigDecimal.valueOf(111.11), Interval.WEEK,  null);
        BudgetRep budgetRep10 = new BudgetRep("Yellow", "#FFFF20", "It's Yellow",
                BigDecimal.valueOf(111.11), Interval.YEAR,  "2017-12-14");
        BudgetRep budgetRep11 = new BudgetRep("Yellow", "#FeFF00", "It's Yellow",
                BigDecimal.valueOf(111.11), Interval.YEAR,  "2017-12-14");

        // Invalid Date
        BudgetRep budgetRep8 = new BudgetRep("Yellow", "#56789a", "It's Yellow",
                BigDecimal.valueOf(111.11), Interval.YEAR,  "2017-15-14");

        // Invalid Colour
        BudgetRep budgetRep7 = new BudgetRep("Yellow", "#FFFG00", "It's Yellow",
                BigDecimal.valueOf(111.11), Interval.YEAR,  "2017-12-14");

        // Used colour selected Scenario
        BudgetRep budgetRep12 = new BudgetRep("Pink", "#FF00FF", "It's pink",
                BigDecimal.valueOf(111.11), Interval.YEAR, "2015-04-14");

        // Used name and date Scenario
        BudgetRep budgetRep13 = new BudgetRep("Black", "#000000", "It's also Black",
                BigDecimal.valueOf(111.11), Interval.QUARTER,  "2018-08-14");

        budgetRep2.setId(budgetRep0.getId());
        budgetService.edit(budgetRep2, user);
        budgetRep3.setId(budgetRep.getId());
        budgetService.edit(budgetRep3, user);
        budgetRep4.setId(budgetRep1.getId());
        budgetService.edit(budgetRep4, user);
        budgetRep5.setId(budgetRep0.getId());
        budgetService.edit(budgetRep5, user);
        budgetRep6.setId(budgetRep.getId());
        budgetService.edit(budgetRep6, user);

        budgetService.edit(budgetRep10, user);
        budgetRep11.setId(budgetRep1.getId());
        budgetService.edit(budgetRep11, null);
        budgetRep8.setId(budgetRep0.getId());
        budgetService.edit(budgetRep8, user);
        budgetRep7.setId(budgetRep1.getId());
        budgetService.edit(budgetRep7, user);
        budgetRep12.setId(budgetRep0.getId());
        budgetService.edit(budgetRep12, user);
        budgetRep13.setId(budgetRep.getId());
        budgetService.edit(budgetRep13, user);
    }

    @Test
    public void getAllTest() throws BasicBudgetBuilderException {
        List<BudgetRep> budgetReps= budgetService.getAll(user);
        Assert.assertEquals(6, budgetReps.size());
    }

    @Test
    public void deleteTest() throws BasicBudgetBuilderException {
        BudgetRep budgetRep = new BudgetRep("Pink", "#EE5555", "It's pink",
                BigDecimal.valueOf(1111.11), Interval.YEAR,  "2017-03-12");
        budgetRep = budgetService.create(budgetRep, user);
        Assert.assertTrue(budgetService.delete(budgetRep.getId()));
        Assert.assertNull(budgetRepository.findById(budgetRep.getId()));
    }
}