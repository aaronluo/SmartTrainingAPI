package com.smarttraining.service;

import com.smarttraining.dao.TrainingDao;
import com.smarttraining.entity.Training;
import com.smarttraining.exception.TrainingNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainingService {

    @Autowired
    private TrainingDao trainingDao;

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
}
