package com.smarttraining.repo;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.smarttraining.entity.User;

@ActiveProfiles("dev")
@RunWith(SpringRunner.class)
@DataJpaTest
public class UserRepoTest {
	@Autowired
	private TestEntityManager entityManager;
	
	@Autowired
	private UserRepo userRepo;
	
	@Test
	public void testGetAllUsers() {
		List<User> users = userRepo.findAll();
		
		assertThat(users.size()).isEqualTo(0);
	}
}
