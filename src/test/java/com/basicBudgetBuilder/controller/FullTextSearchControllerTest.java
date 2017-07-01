//package com.basicBudgetBuilder.controller;
//
//import com.basicBudgetBuilder.BaseTest;
//import com.basicBudgetBuilder.domain.Interval;
//import com.basicBudgetBuilder.representation.BudgetRep;
//import com.basicBudgetBuilder.representation.DebitRep;
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
// * Created by Hanzi Jing on 17/04/2017.
// */
//@RunWith(SpringRunner.class)
//@SpringBootTest
//public class FullTextSearchControllerTest extends BaseTest{
//    @Autowired
//    private ObjectMapper objectMapper;
//
//
//    @Test
//    public void test() throws Exception{
//
////        this.mockMvc.perform(post("/fullTextSearch")
////                .contentType(MediaType.APPLICATION_JSON)
////                .content(objectMapper.writeValueAsString(""))
////                .session(session).with(csrf()));
//    }
//
//}