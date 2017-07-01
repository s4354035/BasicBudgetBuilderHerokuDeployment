//package com.basicBudgetBuilder.controller;
//
//import com.basicBudgetBuilder.BaseTest;
//import com.basicBudgetBuilder.domain.Interval;
//import com.basicBudgetBuilder.representation.BudgetRep;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.google.common.collect.Lists;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import java.math.BigDecimal;
//import java.util.List;
//
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
///**
// * Created by Hanzi Jing on 14/04/2017.
// */
//@RunWith(SpringRunner.class)
//@SpringBootTest
//public class BudgetControllerTest extends BaseTest{
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Test
//    public void addTest() throws Exception{
//        BudgetRep budgetRep = new BudgetRep(
//                "green",
//                "#00FF00",
//                "its green",
//                BigDecimal.valueOf(250.88),
//                Interval.WEEK,
//                true,
//                false,
//                "1943-11-12");
//        BudgetRep budgetRepAddSuccess = new BudgetRep(
//                1,
//                1,
//                1,
//                "green",
//                "#00FF00",
//                "its green",
//                BigDecimal.valueOf(250.88),
//                Interval.WEEK,
//                true,
//                false,
//                "1943-11-12");
//
//        this.mockMvc.perform(post("/budget")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(budgetRep))
//                .session(session).with(csrf()))
//                .andExpect(status().isOk())
//                .andExpect(content().string(objectMapper.writeValueAsString(budgetRepAddSuccess)));
//
//    }
//    @Test
//    public void editTest() throws Exception{
//        BudgetRep editedBudgetRep = new BudgetRep(
//                1,
//                0,
//                0,
//                "green",
//                "#00FF00",
//                "its still green",
//                BigDecimal.valueOf(1540.38),
//                Interval.MONTH,
//                false,
//                false,
//                "2013-12-13");
//
//        BudgetRep editedBudgetRepSuccess = new BudgetRep(
//                1,
//                1,
//                1,
//                "green",
//                "#00FF00",
//                "its still green",
//                BigDecimal.valueOf(1540.38),
//                Interval.MONTH,
//                false,
//                false,
//                "2013-12-13");
//
//        this.mockMvc.perform(post("/budget")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(editedBudgetRep))
//                .session(session).with(csrf()))
//                .andExpect(status().isOk())
//                .andExpect(content().string(objectMapper.writeValueAsString(editedBudgetRepSuccess)));
//    }
//
//    @Test
//    public void getAllTest() throws Exception{
//        this.mockMvc.perform(get("/budget")
//                .session(session).with(csrf()))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    public void deleteTest() throws Exception{
//        List<BudgetRep> toBeDeleted = Lists.newArrayList();
//        BudgetRep budgetRep = new BudgetRep(
//                1,
//                1,
//                1,
//                "green",
//                "#00FF00",
//                "its still green",
//                BigDecimal.valueOf(1540.38),
//                Interval.MONTH,
//                false,
//                false,
//                "2013-12-13");
//        toBeDeleted.add(budgetRep);
//        this.mockMvc.perform(delete("/budget")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(toBeDeleted))
//                .session(session).with(csrf()))
//                .andExpect(status().isOk());
//        System.out.println(true);
//    }
//}