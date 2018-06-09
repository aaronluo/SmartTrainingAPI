package com.smarttraining.dao;

import static org.assertj.core.api.Assertions.assertThat;

import com.smarttraining.entity.Training;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace=Replace.NONE)
@ActiveProfiles("dev")
public class TrainingDaoTest {

    @Autowired
    private TrainingDao trainingDao;
    
    @Test
    public void testNewTraining(){
        Training training = new Training();
        training.setTitle("1-1 class");
        training.setUnitPrice(new BigDecimal(90));
        trainingDao.saveAndFlush(training);
        
        List<Training> trainings = trainingDao.findAll();
        assertThat(trainings.size()).isEqualTo(1);
    }
    
    @Test
    @Sql(scripts="classpath:trainings.sql")
    public void testCRUD() {
        List<Training> trainings = trainingDao.findAll();
        assertThat(trainings.size()).isEqualTo(3);
        
        List<Training> oneoneTrainings = trainingDao.findByTitleLike("%1-1%");
        assertThat(oneoneTrainings.size()).isEqualTo(2);
        
        Training training = trainingDao.getOne(1L);
        assertThat(training).isNotNull();
        trainingDao.delete(training);
        
        oneoneTrainings = trainingDao.findByTitleLike("%1-1%");
        assertThat(oneoneTrainings.size()).isEqualTo(1);
        
        Optional<Training> t = trainingDao.findById(4L);
        assertThat(t.isPresent()).isFalse();
    }
}
