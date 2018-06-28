package com.smarttraining.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.smarttraining.util.Util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class UtlTest {

    @TestConfiguration
    static class UserServiceTestConfig {
        @Bean
        public Util util() {
            return new Util();
        }
        
//        @Bean
//        public JWTUtil JWTUtil() {
//            return new JWTUtil();
//        }
    }
    
    @Autowired
    private Util stUtil;
    
//    @Autowired
//    private JWTUtil jwtUtil;
    
    @Test 
    public void test_username() {
        assertThat(stUtil.isValidUsername("")).isFalse();
        assertThat(stUtil.isValidUsername("aaron")).isFalse();
        assertThat(stUtil.isValidUsername("aaronluo$")).isFalse();
        assertThat(stUtil.isValidUsername("aaronluo")).isTrue();
        assertThat(stUtil.isValidUsername("_aaronluo")).isFalse();
        assertThat(stUtil.isValidUsername("2018_aaronluo")).isFalse();
        assertThat(stUtil.isValidUsername("aaron_2018")).isTrue();
        assertThat(stUtil.isValidUsername("aaron+2018")).isFalse();
    }
    
    @Test
    public void test_password() {
        assertThat(stUtil.isValidPassword("")).isFalse();
        assertThat(stUtil.isValidPassword("1234567")).isFalse();
        assertThat(stUtil.isValidPassword("12345678")).isFalse();
        assertThat(stUtil.isValidPassword("Sword2$8")).isTrue();
        assertThat(stUtil.isValidPassword("Sword2$812333")).isFalse();
    }
    
}
