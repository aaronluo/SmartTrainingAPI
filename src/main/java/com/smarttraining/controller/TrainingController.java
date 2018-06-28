package com.smarttraining.controller;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import com.smarttraining.dto.TrainingDto;
import com.smarttraining.dto.TrainingLogDto;
import com.smarttraining.entity.Training;
import com.smarttraining.entity.TrainingLog;
import com.smarttraining.exception.ApiException;
import com.smarttraining.exception.BadRequestException;
import com.smarttraining.exception.TrainingAccountNotFoundException;
import com.smarttraining.exception.TrainingNotFoundException;
import com.smarttraining.exception.UserNotFoundException;
import com.smarttraining.service.TrainingService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;



@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value="/api/trainings")
public class TrainingController extends GeneicValidator {

    @Autowired
    TrainingService trainingService;
    
    @ApiOperation(value="get all training list")
    @ApiResponses(value={
             @ApiResponse(code=200, message="All avaiable trainings"),
             @ApiResponse(code=403, message="Access forbidden")
     })

    @RequestMapping(value="", method = GET,  produces = "application/json")
    public List<TrainingDto> listTrainings() {
        List<Training> training = trainingService.getAllTrainings();
        
        return training.stream().map(t -> util.geneicMapping(t, TrainingDto.class)).collect(Collectors.toList());
    }
    
    @ApiOperation(value="get a specific training")
    @ApiResponses(value={
             @ApiResponse(code=200, message="training"),
             @ApiResponse(code=403, message="Access forbidden")
     })
    @RequestMapping(value="/{trainingId}", method= GET, produces = "application/json")
    public TrainingDto getTraining(@PathVariable Long trainingId) throws ApiException {
        
        Training training = null;
        
        try {
            training = trainingService.getTraining(trainingId);
            
        } catch (TrainingNotFoundException e) {
            handleException(e);
        }
        
        return util.trainingToDto(training);
    }
    
    @ApiOperation(value="create a new training course")
    @ApiResponses(value={
             @ApiResponse(code=200, message="the created new training"),
             @ApiResponse(code=403, message="Access forbidden"),
             @ApiResponse(code=400, message="Bad request")
     })
    @RequestMapping(value="", method=POST, produces="application/json")
    public TrainingDto newTraining(@RequestBody TrainingDto trainingDto) throws ApiException {
        Training training = null;
        try {
            training = verifyTraining(trainingDto);
            training = trainingService.save(training);
        } catch (BadRequestException e) {
            handleException(e);
        }
        
        return util.geneicMapping(training, TrainingDto.class);
    }
    
    @RequestMapping(value="{trainingId}", method=PUT, produces="application/json")
    public TrainingDto updateTraining(@PathVariable Long trainingId, 
            @RequestBody TrainingDto trainingDto) throws ApiException {
        Training training = null;
        try {
            training = verifyTraining(trainingDto);
            training = trainingService.update(trainingId, training);
        } catch (BadRequestException |  TrainingNotFoundException e) {
            handleException(e);
        } 
        
        return util.geneicMapping(training, TrainingDto.class);
    }
    
    @ApiOperation(value="add a training log")
    @ApiResponses(value={
             @ApiResponse(code=200, message="the new training log"),
             @ApiResponse(code=403, message="Access forbidden"),
             @ApiResponse(code=400, message="Bad request"),
             @ApiResponse(code=400, message="Trainer and/or Trainee and/or Training not found")
     })
    @RequestMapping(value="/{trainingId}/logs", method=POST, produces = "application/json")
    public TrainingLogDto addTrainingLog(@PathVariable Long trainingId, 
            @RequestBody TrainingLogDto trainingLogDto) throws ApiException{
        TrainingLog log = null;
        
        try {
            log = verifyTrainingLog(trainingLogDto);
            
            log = trainingService.addLog(trainingId, log);
            
        } catch (BadRequestException | TrainingNotFoundException | UserNotFoundException | TrainingAccountNotFoundException e) {
            handleException(e);
        } 
        
        return util.geneicMapping(log, TrainingLogDto.class);
    }
}
