//package com.basicBudgetBuilder;
//
//import com.basicBudgetBuilder.domain.Role;
//import com.basicBudgetBuilder.domain.User;
//import com.basicBudgetBuilder.repository.DeleteDataForUserRepository;
//import com.basicBudgetBuilder.service.UserService;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.After;
//import org.junit.Before;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.mock.web.MockHttpServletRequest;
//import org.springframework.mock.web.MockHttpSession;
//import org.springframework.security.web.FilterChainProxy;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.web.context.WebApplicationContext;
//
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
///**
// * Created by Hanzi Jing on 14/04/2017.
// */
//@RunWith(SpringRunner.class)
//@SpringBootTest
////@AutoConfigureMockMvc
//public class BaseTest {
//
//    protected MockMvc mockMvc;
//
//    @Autowired
//    protected UserService userService;
//
//    @Autowired
//    protected FilterChainProxy springSecurityFilterChain;
//
//    @Autowired
//    protected WebApplicationContext wac;
//
//    @Autowired
//    protected ObjectMapper objectMapper;
//
//    @Autowired
//    protected DeleteDataForUserRepository deleteDataForUserRepository;
//    protected MockHttpSession session;
//
//    protected MockHttpServletRequest request;
//
//    protected User user;
//
//    @Before
//    public void setup()throws Exception {
//        String email = "hanzi.jing94@gmail.com";
//        String password = "password";
//        String name = "Hanzi";
//        user = userService.findByEmail(email);
//        if(user == null) {
//            user = new User(email, password, Role.USER, name);
//            userService.save(user);
//        }
//        mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).addFilters(this.springSecurityFilterChain).build();
//        session = (MockHttpSession) mockMvc.perform(formLogin("/login").user(email).password(password))
//                .andDo(print()).andExpect(status().isMovedTemporarily()).andReturn().getRequest().getSession();
//
//    }
//
//    @After
//    public void cleanData() throws Exception{
//        deleteDataForUserRepository.deleteAllForUser(user);
//    }
//
//}
