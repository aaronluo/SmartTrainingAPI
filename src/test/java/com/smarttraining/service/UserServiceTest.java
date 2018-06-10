package com.smarttraining.service;
import static org.assertj.core.api.Assertions.assertThat;

import com.smarttraining.dao.UserDao;
import com.smarttraining.entity.User;
import com.smarttraining.exception.InvalidUserOrPasswordException;
import com.smarttraining.exception.UserNotFoundException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

@RunWith(SpringRunner.class)
//@DataJpaTest
//@AutoConfigureTestDatabase(replace=Replace.NONE)
//@ActiveProfiles("dev")
public class UserServiceTest {

    //在进行单元测试的时候，并没有一个完整的container来运行所有的组件
    //使用TestConfiguration可以帮测试用例产生一个需要Autowired的Service Bean实例
    @TestConfiguration
    static class UserServiceTestConfig {
        @Bean
        public UserService userService() {
            return new UserService();
        }
    }
    
    @Autowired
    UserService userService;
    
//    @Autowired
//    UserDao userDao;
//    
        // 可以用和测试Repository一样的方式启动数据库进行Servcie层测试，但是
        // 考虑到Service层的测试其实不关心Repository的具体实现，所以可以用
        // MockBean来Mock Repository层    
//    @Test
//    @Sql(scripts="classpath:users_create.sql")
//    public void test_isUserExisting() {
//        assertThat(userDao.findByUsername("eric").isPresent()).isTrue();
//    }
    
    @MockBean
    UserDao userDao;
    
    @Before
    public void setup(){
        User user = new User();
        user.setUsername("eric");
        user.setPassword("");
        Mockito.when(userDao.findByUsername("eric")).thenReturn(Optional.of(user));
        Mockito.when(userDao.findByUsername("tom")).thenReturn(Optional.ofNullable(null));
    }
    
    @Test
    public void test_isUserExisting() {
        assertThat(userService.isUserExisting("eric")).isTrue();
        assertThat(userService.isUserExisting("tom")).isFalse();
    }
    
    @Test(expected = UserNotFoundException.class)
    public void test_login_userNotFound() throws UserNotFoundException, InvalidUserOrPasswordException {
        userService.login("tom", "");
    }
    
    @Test
    public void test_login() throws UserNotFoundException, InvalidUserOrPasswordException {
        userService.login("eric", "");
    }
    
    @Test(expected = InvalidUserOrPasswordException.class)
    public void test_loginWithWrongPassword() throws UserNotFoundException, InvalidUserOrPasswordException {
        userService.login("eric", "123");
    }
}
