package com.smarttraining.service;

import com.smarttraining.dao.TrainingDao;
import com.smarttraining.dao.UserDao;
import com.smarttraining.entity.Training;
import com.smarttraining.entity.TrainingAccount;
import com.smarttraining.entity.TrainingLog;
import com.smarttraining.entity.User;
import com.smarttraining.exception.TrainingAccountNotFoundException;
import com.smarttraining.exception.TrainingNotFoundException;
import com.smarttraining.exception.UserNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainingService {

    @Autowired
    private TrainingDao trainingDao;

    @Autowired
    private UserDao userDao;
    
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
       
        userDao.saveAndFlush(trainee);
       
        
        return log;
    }
}
