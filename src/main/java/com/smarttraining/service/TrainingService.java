package com.smarttraining.service;

import com.querydsl.core.BooleanBuilder;
import com.smarttraining.dao.TrainingDao;
import com.smarttraining.dao.UserDao;
import com.smarttraining.entity.QTraining;
import com.smarttraining.entity.QTrainingAccount;
import com.smarttraining.entity.QUser;
import com.smarttraining.entity.Training;
import com.smarttraining.entity.TrainingAccount;
import com.smarttraining.entity.TrainingLog;
import com.smarttraining.entity.User;
import com.smarttraining.exception.TrainingAccountNotFoundException;
import com.smarttraining.exception.TrainingNotFoundException;
import com.smarttraining.exception.UserNotFoundException;
import com.smarttraining.querymodel.BaseQueryModel;
import com.smarttraining.util.Util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainingService {

    @Autowired
    private TrainingDao trainingDao;

    @Autowired
    private UserDao userDao;
    
    @Autowired
    private Util util;
    
    public List<Training> getAllTrainings() {
        return trainingDao.findAll();
    }

    public Training getTraining(Long trainingId) throws TrainingNotFoundException {
        Training training = trainingDao.findOne(trainingId);
        if(null == training) {
            throw new TrainingNotFoundException(trainingId);
        }
        return training;
    }

    public Training save(Training training) {
        return trainingDao.saveAndFlush(training);
    }

    public TrainingLog addLog(Long trainingId, TrainingLog log) 
            throws TrainingNotFoundException, UserNotFoundException, TrainingAccountNotFoundException {
        Training training = this.getTraining(trainingId);
        User trainer = userDao.findOne(log.getTrainer().getId());
        if(null == trainer) {
            throw new UserNotFoundException("" + log.getTrainer().getId());
        }
        
        User trainee = userDao.findOne(log.getTrainee().getId());
        if(null == trainee) {
            throw new UserNotFoundException("" + log.getTrainee().getId());
        }
        
        TrainingAccount account = trainee.getTrainingAccounts().stream()
                .filter(c -> c.getTraining().getId() == trainingId).findFirst().orElse(null);
        
        if(null == account) {
            throw new TrainingAccountNotFoundException(trainee.getUsername(), -999L);
        }
        
        log.setTrainee(trainee);
        log.setTrainer(trainer);
        account.AddTrainingLog(log);
       
       trainee =  userDao.saveAndFlush(trainee);
       
       account = trainee.getTrainingAccounts().stream()
               .filter(c -> c.getTraining().getId() == trainingId).findFirst().orElse(null);

        return ((List<TrainingLog>)account.getTrainingLogs()).get(0);
    }

    public Training update(Long trainingId, Training updatedTraining) 
            throws TrainingNotFoundException {
        Training training = this.getTraining(trainingId);
        
        if(updatedTraining.getUnitPrice() != null) {
            training.setUnitPrice(updatedTraining.getUnitPrice());
        }
        
        if(updatedTraining.getLimitation() > 0) {
            training.setLimitation(updatedTraining.getLimitation());
        }
        
        if(!util.isEmpty(updatedTraining.getDescription())) {
            training.setDescription(updatedTraining.getDescription());
        }
        
        if(updatedTraining.getStartDate() != null) {
            training.setStartDate(updatedTraining.getStartDate());
        }
        
        if(updatedTraining.getEndDate() != null) {
            training.setEndDate(updatedTraining.getEndDate());
        }
        
        training = trainingDao.saveAndFlush(training);
        
        return training;
    }

    public Page<User> listUsers(Long trainingId, BaseQueryModel query) {
       QUser user = QUser.user;
       QTraining training = QTraining.training;
       QTrainingAccount account = QTrainingAccount.trainingAccount;
       
       BooleanBuilder builder = new BooleanBuilder();
       builder.and(user.trainingAccounts.any().training.id.eq(trainingId));
        
       return userDao.findAll(builder.getValue(), cratePageRequest(query));
    }
    
    private PageRequest cratePageRequest(BaseQueryModel query) {
        return new PageRequest(query.getPage() - 1, query.getSize(), Sort.Direction.DESC, "id");
    }
}
