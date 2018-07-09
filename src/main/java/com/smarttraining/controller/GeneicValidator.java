package com.smarttraining.controller;

import com.smarttraining.dto.DepositeLogDto;
import com.smarttraining.dto.TrainingAccountDto;
import com.smarttraining.dto.TrainingDto;
import com.smarttraining.dto.TrainingLogDto;
import com.smarttraining.entity.DepositeLog;
import com.smarttraining.entity.Training;
import com.smarttraining.entity.TrainingAccount;
import com.smarttraining.entity.TrainingLog;
import com.smarttraining.exception.ApiError;
import com.smarttraining.exception.ApiException;
import com.smarttraining.exception.BadRequestException;
import com.smarttraining.exception.InvalidUserOrPasswordException;
import com.smarttraining.exception.InvalidUsernamePasswordExcpetion;
import com.smarttraining.exception.PropertyAlreadyExistingException;
import com.smarttraining.exception.TrainingAccountAlreadyExistingExecption;
import com.smarttraining.exception.TrainingAccountNotFoundException;
import com.smarttraining.exception.TrainingNotFoundException;
import com.smarttraining.exception.UserAlreadyExistingException;
import com.smarttraining.exception.UserNotFoundException;
import com.smarttraining.querymodel.BaseQueryModel;
import com.smarttraining.querymodel.DepositeQueryModel;
import com.smarttraining.querymodel.TrainingLogQueryModel;
import com.smarttraining.querymodel.TrainingQueryModel;
import com.smarttraining.querymodel.UserQueryModel;
import com.smarttraining.util.Util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;

@Component
public abstract class  GeneicValidator {
    
    @Autowired
    Util util;
    
    protected void handleException(HttpStatus status, Exception cause) throws ApiException {
        throw new ApiException(new ApiError(status,  cause.getMessage()), cause);
    }
    
    protected void handleException(HttpStatus status, String message) throws ApiException {
        throw new ApiException(new ApiError(status,  message), null);
    }
    
    protected void handleException(Exception cause) throws ApiException {
        if(cause instanceof BadRequestException ||
                cause instanceof InvalidUsernamePasswordExcpetion ||
                cause instanceof InvalidUserOrPasswordException ||
                cause instanceof PropertyAlreadyExistingException ||
                cause instanceof TrainingAccountAlreadyExistingExecption ||
                cause instanceof UserAlreadyExistingException) {
            this.handleException(HttpStatus.BAD_REQUEST, cause);
        }else if(cause instanceof TrainingAccountNotFoundException ||
                cause instanceof TrainingNotFoundException ||
                cause instanceof UserNotFoundException) {
            this.handleException(HttpStatus.NOT_FOUND, cause);
        }else {
            this.handleException(HttpStatus.INTERNAL_SERVER_ERROR, cause);
        }
    }
    
    protected void verifyTrainingLogQueryModel(TrainingLogQueryModel query) throws BadRequestException {
        this.verifyPageAndSize(query);
        this.verifyCreateDateRange(query);
        if(query.getTrainerId() != null && query.getTrainerId().longValue() <= 0) {
            throw new BadRequestException("The trainner id can not be negative"); 
        }
    }
    
    protected  void verifyTrainingQueryModel(TrainingQueryModel query) throws BadRequestException {
        this.verifyPageAndSize(query);
        this.verifyCreateDateRange(query);
    }
    
    protected TrainingLog verifyTrainingLog(TrainingLogDto log) throws BadRequestException {
        if(log.getTrainerId() == null || log.getTrainerId().longValue() <= 0) {
            throw new BadRequestException("The trainner id can not be null or zero"); 
        }
        if(log.getTraineeId() == null || log.getTraineeId().longValue() <= 0) {
            throw new BadRequestException("The trainee id can not be null or zero"); 
        }
        
        return util.trainingLogDtoToPo(log);
    }
    
    
    protected DepositeLog verifyDepositeLog(DepositeLogDto depositeDto) throws BadRequestException {

        if(depositeDto.getAmount().compareTo(BigDecimal.ZERO) == -1) {
            throw new BadRequestException("The deposite amount can not be negative");
        }
        
        return util.geneicMapping(depositeDto, DepositeLog.class);
    }
    
    protected Training verifyTraining(TrainingDto trainingDto) throws BadRequestException {
        if(util.isEmpty(trainingDto.getTitle())){
            throw new BadRequestException("The title of a training can not be empty");
        }
        
        if(trainingDto.getUnitPrice().compareTo(BigDecimal.ZERO) == -1) {
            throw new BadRequestException("The unit price of a training can not be negative");
        }
        
        if(trainingDto.getLimitation() <= 0) {
            throw new BadRequestException("The limitation of a training can not be negative");
        }
        
        if(trainingDto.getStartDate() == null || trainingDto.getStartDate().isBefore(LocalDate.now(ZoneId.systemDefault()))) {
            throw new BadRequestException("The start date  of a training must be set a date of today or future");
        }
        
        if(trainingDto.getEndDate() != null && trainingDto.getEndDate().isBefore(trainingDto.getStartDate())) {
            throw new BadRequestException("The end date  of a training must be after the start date");
        }
        
        return util.geneicMapping(trainingDto, Training.class);
    }
    
    protected void verifyDepositeQueryModel(DepositeQueryModel query) throws BadRequestException {
        verifyPageAndSize(query);
        verifyCreateDateRange(query);
        
        if(query.getAmount().getMinVal() != null &&
                query.getAmount().getMaxVal() != null) {
            if(query.getAmount().getMinVal().compareTo(query.getAmount().getMaxVal()) >= 0) {
                throw new BadRequestException("The min value should be less than the max value");
            }
        }
    }
    
    
    protected void verifyUserQueryModel(UserQueryModel userQueryModel) throws BadRequestException {
        verifyPageAndSize(userQueryModel);
        verifyCreateDateRange(userQueryModel);
    }




    /**
     * verify if passed in accountDto has correct information for create a new account entity
     * @param accountDto
     * @return
     * @throws BadRequestException
     */
    protected TrainingAccount verifyAccount(TrainingAccountDto accountDto) throws BadRequestException {
        TrainingAccount account = util.trainingAccountDtoToTrainingAccount(accountDto);        
        if(account.getUnitPrice().compareTo(BigDecimal.ZERO) == -1) {
            throw new BadRequestException("The unit price can not be negative");
        }
        
        if(account.getBalance().compareTo(BigDecimal.ZERO) == -1) {
            throw new BadRequestException("The balance can not be negative");
        }
        
        if(null == account.getValidBeginDate()) {
            throw new BadRequestException("The start date can not null");
        }
        
        if(null != account.getValidBeginDate() 
                && account.getValidBeginDate().isBefore(LocalDate.now(ZoneId.systemDefault()))) {
            throw new BadRequestException("The start date can not be previous date of today");
        }
        
        
        if(null != account.getValidBeginDate() &&
                null != account.getValidEndDate() && 
                account.getValidBeginDate().isAfter(account.getValidEndDate())) {
            throw new BadRequestException("The start date must be before the end date");
        }
        
        if(account.getTraining().getId() <=0 ) {
            throw new BadRequestException(String.format("The select training[%d] is invalid", account.getTraining().getId()));
        }
        
        return account;
    }
    
    protected void verifyPageAndSize(BaseQueryModel queryModel)
            throws BadRequestException {
        if (queryModel.getPage() <= 0) {
            throw new BadRequestException("The page index can not be negative or zero");
        }
        
        if(queryModel.getSize() <= 0) {
            throw new BadRequestException("The page size can not be negative or zero");
        }
    }
    
    protected void verifyCreateDateRange(BaseQueryModel queryModel)
            throws BadRequestException {
        if(queryModel.getCreateDate() != null 
                && queryModel.getCreateDate().getMinVal() != null 
                && queryModel.getCreateDate().getMaxVal() != null
                && queryModel.getCreateDate().getMinVal().isAfter(queryModel.getCreateDate().getMaxVal())) {
            throw new BadRequestException("The min value of the create date should less than the max one");
        }
    }
}
