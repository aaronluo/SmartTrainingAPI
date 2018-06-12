package com.smarttraining.dao;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.smarttraining.entity.DepositeLog;
import com.smarttraining.entity.QTraining;
import com.smarttraining.entity.Training;
import com.smarttraining.entity.TrainingAccount;
import com.smarttraining.entity.TrainingLog;
import com.smarttraining.entity.User;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace=Replace.NONE)
@ActiveProfiles("dev")
public class TrainingAccountDaoTest {

    @Autowired
    private  TrainingDao trainingDao;
    @Autowired
    private  UserDao userDao;
    
    @Before
    public  void setup() {
        // create a trainee - seanluo
        User trainee = new User();
        trainee.setUsername("seanluo");
        trainee.setPassword("123");
        userDao.saveAndFlush(trainee);
        
        //create a trainer - alex
        User trainer = new User();
        trainer.setUsername("alex");
        trainer.setPassword("123");
        userDao.saveAndFlush(trainer);
        
        //create 2 trainings
        Training training_1 = new Training();
        training_1.setTitle("1-1 training");
        training_1.setUnitPrice(new BigDecimal(100.00));
        trainingDao.saveAndFlush(training_1);
        
        Training training_2 = new Training();
        training_2.setTitle("group training");
        training_2.setUnitPrice(new BigDecimal(85.00));
        trainingDao.saveAndFlush(training_2);
    }
    
    @Test
    public void test_addTrainingAccount() {
        //find 1-1 training
        QTraining xTraining = QTraining.training;
        
        BooleanExpression sameTitle = xTraining.title.eq("1-1 training");
        Training training = trainingDao.findOne(sameTitle);
        
        assertThat(training).isNotNull();
        assertThat(training.getUnitPrice()).isEqualTo(new BigDecimal(100.00));
        
        //sean want to take 1-1 training
        Optional<User> sean = userDao.findByUsername("seanluo");
        assertThat(sean.isPresent()).isTrue();
        
        sean.get().getTrainingAccounts().forEach(account -> {
            assertThat(account.getId()).isNotEqualTo(training.getId());
        });
        
        TrainingAccount account = new TrainingAccount();
        account.setTraining(training);
        account.setUnitPrice(new BigDecimal(90.00));
        account.setValidBeginDate(LocalDate.now());
        account.setValidEndDate(LocalDate.now().plusYears(1));
        sean.get().addTrainingAccount(account);
        
        userDao.saveAndFlush(sean.get());
        
        User sean_2 = userDao.findByUsername("seanluo").get();
        
        assertThat(sean_2.getUsername()).isEqualTo("seanluo");
        assertThat(sean_2.getTrainingAccounts().size()).isEqualTo(1);
        List<TrainingAccount> accounts = (List<TrainingAccount>) sean_2.getTrainingAccounts();
        assertThat(accounts.get(0).getTraining().getTitle()).isEqualTo("1-1 training");
        
        //给账户充值
        DepositeLog deposite = new DepositeLog();
        deposite.setAmount(new BigDecimal(8800.00));
        accounts.get(0).AddDepositeLog(deposite);
        assertThat(accounts.get(0).getBalance()).isEqualTo(new BigDecimal(8800.00));
        
        userDao.saveAndFlush(sean_2);
        
        User sean_3 = userDao.findByUsername("seanluo").get();
        accounts = (List<TrainingAccount>) sean_3.getTrainingAccounts();
        assertThat(accounts.get(0).getBalance()).isEqualTo(new BigDecimal(8800.00));
        
        //上了一次课
        User trainer = userDao.findByUsername("alex").get();
        TrainingLog trainingLog = new TrainingLog();
        trainingLog.setTrainer(trainer);
        
        accounts.get(0).AddTrainingLog(trainingLog);
        
        userDao.saveAndFlush(sean_3);
        
        User sean_4 = userDao.findByUsername("seanluo").get();
        accounts = (List<TrainingAccount>) sean_3.getTrainingAccounts();
        assertThat(accounts.get(0).getBalance()).isEqualTo(new BigDecimal(8800.00).subtract(new BigDecimal(90.00)));
    }
}
