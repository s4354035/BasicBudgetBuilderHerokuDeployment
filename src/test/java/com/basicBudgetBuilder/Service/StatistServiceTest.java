package com.basicBudgetBuilder.Service;

import com.basicBudgetBuilder.domain.User;
import com.basicBudgetBuilder.exceptions.BasicBudgetBuilderException;
import com.basicBudgetBuilder.representation.StatRep;
import com.basicBudgetBuilder.service.StatisticsService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by Hanzi Jing on 11/05/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Service
public class StatistServiceTest {
    @Autowired
    StatisticsService statisticsService;

    @Test
    public void test() throws BasicBudgetBuilderException{
        User user = new User();
        user.setId(1);
        StatRep rep = statisticsService.getStats(user, null);
        Assert.assertNotNull(rep.getTotalSpent());
    }
}
