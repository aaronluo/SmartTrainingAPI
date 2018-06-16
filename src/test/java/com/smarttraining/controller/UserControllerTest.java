package com.smarttraining.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.smarttraining.dao.UserDao;
import com.smarttraining.dto.UserToken;
import com.smarttraining.entity.User;
import com.smarttraining.security.AppSecurityCfg;
import com.smarttraining.service.UserService;
import com.smarttraining.util.JWTUtil;
import com.smarttraining.util.TestUtil;
import com.smarttraining.util.Util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
@Import(AppSecurityCfg.class)
public class UserControllerTest {

    @TestConfiguration
    static class UserServiceTestConfig {
        @Bean
        public Util util() {
            return new Util();
        }
        
        @Bean
        public JWTUtil JWTUtil() {
            return new JWTUtil();
        }
        
        
    }
    
    @Autowired
    private MockMvc mvc;
    
    @MockBean
    UserDao userDao;
    
    @MockBean
    private UserService userService;
    
    @Test
    public void test_loginWithRightUser() throws Exception{
        UserToken token = new UserToken();
        token.setUsername("aaronluo_2018");
        Mockito.when(userService.isUserExisting("aaronluo_2018")).thenReturn(true);
        Mockito.when(userService.login("aaronluo_2018", "sword2$8")).thenReturn(token);
        
        User user = new User();
        user.setUsername("aaronluo_2018");
        user.setPassword("sword2$8");
        user.setId(123455L);
        user.setCreateDate(LocalDateTime.now());
        
//        mvc.perform(
//                post("/users/login")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(TestUtil.objToJson(user))
//                ).andExpect(status().isOk())
//                 .andExpect(jsonPath("$", hasSize(1)));
        System.out.println(TestUtil.objToJson(user));
        mvc.perform(get("/users/hello")).andExpect(status().isOk());
        mvc.perform(post("/users/login").contentType(MediaType.APPLICATION_JSON).content("{\"username\":\"aaronluo_2018\", \"password\":\"sword2$8\"}")).andExpect(status().isOk());
    }
    
}
