package com.smarttraining.controller;

import com.smarttraining.dto.TrainingDto;
import com.smarttraining.entity.Training;
import com.smarttraining.exception.ApiError;
import com.smarttraining.exception.ApiException;
import com.smarttraining.exception.QueryModelException;
import com.smarttraining.exception.TrainingNotFoundException;
import com.smarttraining.service.TrainingService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value="/trainings")
public class TrainingController extends GeneicValidator {

    @Autowired
    TrainingService trainingService;
    
    @ApiOperation(value="get all training list")
    @ApiResponses(value={
             @ApiResponse(code=200, message="All avaiable trainings"),
             @ApiResponse(code=403, message="Access forbidden")
     })
    @RequestMapping(value="/", method = RequestMethod.GET,  produces = "application/json")
    public List<TrainingDto> listTrainings() {
        List<Training> training = trainingService.getAllTrainings();
        
        return training.stream().map(t -> util.geneicMapping(t, TrainingDto.class)).collect(Collectors.toList());
    }
    
    @ApiOperation(value="get a specific training")
    @ApiResponses(value={
             @ApiResponse(code=200, message="training"),
             @ApiResponse(code=403, message="Access forbidden")
     })
    @RequestMapping(value="/{trainingId}", method=RequestMethod.GET, produces = "application/json")
    public TrainingDto getTraining(@PathVariable Long trainingId) throws ApiException {
        try {
            return  util.getMapper().map(trainingService.getTraining(trainingId), TrainingDto.class);
            
        } catch (TrainingNotFoundException e) {
            throw new ApiException(new ApiError(HttpStatus.NOT_FOUND, e.getMessage()), e);
        }
    }
    
    @ApiOperation(value="create a new training course")
    @ApiResponses(value={
             @ApiResponse(code=200, message="the created new training"),
             @ApiResponse(code=403, message="Access forbidden"),
             @ApiResponse(code=400, message="Bad request")
     })
    @RequestMapping(value="/", method=RequestMethod.POST, produces="application/json")
    public TrainingDto newTraining(@RequestBody TrainingDto trainingDto) throws ApiException {
        Training training = null;
        try {
            training = verifyTraining(trainingDto);
            training = trainingService.save(training);
        } catch (QueryModelException e) {
            throw new ApiException(new ApiError(HttpStatus.BAD_REQUEST, e.getMessage()), e);
        }
        
        return util.geneicMapping(training, TrainingDto.class);
    }
}
