package com.smarttraining.dao;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.smarttraining.entity.QRole;
import com.smarttraining.entity.QUser;
import com.smarttraining.entity.Role;
import com.smarttraining.entity.User;
import com.smarttraining.entity.UserProperty;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace=Replace.NONE)
@ActiveProfiles("dev")
public class UserDaoTest {
//	@Autowired
//	private TestEntityManager entityManager;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private RoleDao roleDao;
	
	@Test
	@Sql(scripts="classpath:users_create.sql")
	public void testGetAllUsers() {
		List<User> users = userDao.findAll();
		
		assertThat(users.size()).isEqualTo(9);
	}
	
	@Test
	@Sql(scripts="classpath:users_create.sql")
	public void testGetAllUsersPaged() {
	    Page<User> users = userDao.findAll(new PageRequest(0, 3));
	    
	    assertThat(users.getSize()).isEqualTo(3); 
	    assertThat(users.getTotalPages()).isEqualTo(3);
	    assertThat(users.getTotalElements()).isEqualTo(9);
	    
	    assertThat(users.getContent().get(0).getId()).isEqualTo(9L);
	}
	
	@Test
	@Sql(scripts="classpath:roles.sql")
    public void testNewUser() {
        User user = new User();
        user.setUsername("aaronluo");
        user.setPassword("123abc");
        
        Role roleTrainer = roleDao.findByName(Role.RoleType.TRAINER).orElse(null);
        
        assertThat(roleTrainer).isNotNull();
        user.addRole(roleTrainer);
        
	    user = userDao.saveAndFlush(user);
	    
	    assertThat(user.getId()).isGreaterThan(0L);
	    assertThat(user.isActive()).isEqualTo(true);
	    assertThat(user.getCreateDate()).isNotNull();
	    assertThat(user.getUpdateDate()).isNotNull();
	    assertThat(user.getCreateDate()).isEqualTo(user.getUpdateDate());
	    assertThat(user.getRoles().size()).isEqualTo(1);
	    assertThat((user.getRoles().toArray(new Role[user.getRoles().size()])[0]).getName()).isEqualTo(Role.RoleType.TRAINER);
	    
	    //test update date being injected automatically
	    user.setPassword("11111");
	    user = userDao.saveAndFlush(user);
	    
	    assertThat(user.getPassword()).isNotEqualTo("123abc");
	    assertThat(user.getUpdateDate()).isNotEqualTo(user.getCreateDate());
	}
	
	@Test
	@Sql(scripts="classpath:users_create.sql")
	public void testGetUserByUsername() {
	    String username = "aaron";
	    Optional<User> user = userDao.findByUsername(username);
	    assertThat(user.get().getId()).isEqualTo(3L);
	    
	    //no user with the username
	    username = "aaronluo";
	    user = userDao.findByUsername(username);
	    assertThat(user.isPresent()).isFalse();
	    
	    //inative user 'tom'
	    username = "tom";
	    user = userDao.findByUsername(username);
	    assertThat(user.isPresent()).isFalse();
	}
	
	@Test
	@Sql(scripts="classpath:users_property_create.sql")
	public void testUserAndUserProerty() {
	    Optional<User> userOptional = userDao.findByUsername("aaronluo");
	    assertThat(userOptional.isPresent()).isTrue();
	    User user = userOptional.get();
	    
	    assertThat(user.getId()).isEqualTo(1L);
	    assertThat(user.getProperties().size()).isEqualTo(2);
	}

    @Test
    @Sql(scripts = "classpath:users_property_create.sql")
    public void testDeleteUserProerty() {
        Optional<User> userOptional = userDao.findByUsername("aaronluo");
        assertThat(userOptional.isPresent()).isTrue();
        User user = userOptional.get();
        user.getProperties().forEach(userProp -> {
            if (userProp.getName().equals("age")) {
                user.removeProperty(userProp);
            }
        });
        
        assertThat(user.getProperties().size()).isEqualTo(1);
        userDao.saveAndFlush(user);
        
        userOptional = userDao.findByUsername("aaronluo");
        assertThat(userOptional.get().getProperties().size()).isEqualTo(1);
    }
	
    @Test
    public void testAddUserProperty() {
        User user = new User();
        user.setUsername("aaronluo");
        user.setPassword("123abc");
        UserProperty userProp = new UserProperty();
        userProp.setName("age");
        userProp.setValue("40");
        user.addProperty(userProp);
        
        user = userDao.saveAndFlush(user);
        
        Optional<User> userProxy = userDao.findByUsername("aaronluo");
        
        assertThat(userProxy.isPresent()).isTrue();
        
        user = userProxy.get();
        
        assertThat(user.getProperties().size()).isEqualTo(1);
    }
	
    @Test
    @Sql(scripts = "classpath:users_property_create.sql")
    public void test_addValidProperty() {
        User user = userDao.findByUsername("aaronluo").get();
        UserProperty userProp = new UserProperty();
        userProp.setName("family_name");
        userProp.setValue("罗");
        
        user.addProperty(userProp);
        
        user = userDao.saveAndFlush(user);
        
        assertThat(user.getProperties().size()).isEqualTo(3);
    }
    
	@Test
	public void testUserDeletion () {
        User user = new User();
        user.setUsername("aaronluo");
        user.setPassword("123abc");
        user = userDao.saveAndFlush(user);

        userDao.delete(user);
        
        assertThat(user.isActive()).isFalse();
        
        Optional<User> userProxy = userDao.findByUsername(user.getUsername());
        
        assertThat(userProxy.isPresent()).isFalse();
	}
	
	@Test
	@Sql(scripts="classpath:users_create.sql")
	public void testQuery() {
	    QUser user = QUser.user;
	    QRole role = QRole.role;
	    BooleanExpression hasUsername = user.username.like("%aaron%");
	    Page<User> users = userDao.findAll(hasUsername, new PageRequest(0, 5));
	    assertThat(users.getContent().size()).isEqualTo(1);
	    
	    LocalDateTime firstDayOf2018 = LocalDateTime.of(LocalDate.of(2018, Month.JANUARY, 1), LocalTime.of(23, 59));
	    BooleanExpression createBeforeDate = user.createDate.lt(firstDayOf2018);
	    users = userDao.findAll(createBeforeDate, new PageRequest(0, 5));
	    assertThat(users.getContent().size()).isEqualTo(1);
	    
	    users = userDao.findAll(hasUsername.and(createBeforeDate), new PageRequest(0, 5));
	    assertThat(users.getContent().size()).isZero();
	    
	    List<String> roles = new ArrayList<String>();
	    roles.add("TRAINER");
	    
	    QRole role_1 = new QRole("TRAINER");
	    QRole role_2 = new QRole("TRAINEE");
	    
	    BooleanExpression isTrainer = user.roles.contains(JPAExpressions.selectFrom(role).where(role.name.in("TRAINER")));
	    
	    users = userDao.findAll(hasUsername.and(isTrainer), new PageRequest(0, 5));
	    
	    assertThat(users.getContent().size()).isEqualTo(1);
	    assertThat(users.getContent().get(0).getUsername()).isEqualTo("aaron");
	}
}
