package com.smarttraining.service;
import static org.assertj.core.api.Assertions.assertThat;

import com.smarttraining.dao.UserDao;
import com.smarttraining.entity.User;
import com.smarttraining.exception.InvalidUsernamePasswordExcpetion;
import com.smarttraining.exception.UserAlreadyExistingException;
import com.smarttraining.util.Util;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@RunWith(SpringRunner.class)
//@DataJpaTest
//@AutoConfigureTestDatabase(replace=Replace.NONE)
//@ActiveProfiles("dev")
public class UserServiceTest {

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    //在进行单元测试的时候，并没有一个完整的container来运行所有的组件
    //使用TestConfiguration可以帮测试用例产生一个需要Autowired的Service Bean实例
    @TestConfiguration
    static class UserServiceTestConfig {
        @Bean
        public UserService userService() {
            return new UserService();
        }
        @Bean
        public Util util() {
            return new Util();
        }
       
    }
    
    @Autowired
    UserService userService;
    
    @Autowired
    Util util;
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
        user.setPassword(passwordEncoder.encode(""));
        user.setCreateDate(LocalDateTime.now(ZoneId.systemDefault()));
        Mockito.when(userDao.findByUsername("eric")).thenReturn(Optional.of(user));
        Mockito.when(userDao.findByUsername("tom")).thenReturn(Optional.ofNullable(null));
        Mockito.when(userDao.findByUsername("aaronluo_2018")).thenReturn(Optional.ofNullable(null));
        User newUser = new User();
        newUser.setUsername("aaronluo_2018");
        newUser.setPassword("sword2$8");
        Mockito.when(userDao.saveAndFlush(user)).thenReturn(user);
    }
    
    @Test
    public void test_isUserExisting() {
        assertThat(userService.isUserExisting("eric")).isTrue();
        assertThat(userService.isUserExisting("tom")).isFalse();
    }
    

    @Test(expected = InvalidUsernamePasswordExcpetion.class)
    public void test_registerWithWrongUsername() throws UserAlreadyExistingException, InvalidUsernamePasswordExcpetion {
        User user = new User();
        user.setUsername("tom");
        
        userService.register(user);
    }
    @Test(expected = UserAlreadyExistingException.class)
    public void test_registerWithUsedUsername() throws UserAlreadyExistingException, InvalidUsernamePasswordExcpetion {
        User user = new User();
        user.setUsername("eric");
        
        userService.register(user);
    }
    
    @Test(expected=InvalidUsernamePasswordExcpetion.class)
    public void test_registerWithBadPassword() throws UserAlreadyExistingException, InvalidUsernamePasswordExcpetion {
        User user = new User();
        user.setUsername("aaronluo_2018");
        user.setPassword("swor23");
        
        userService.register(user);
    }
    
    @Test()
    public void test_registerWithCoorectUsername() throws UserAlreadyExistingException, InvalidUsernamePasswordExcpetion {
        User user = new User();
        user.setUsername("aaronluo_2018");
        user.setPassword("sword2$8");
        
        userService.register(user);
    }
}
