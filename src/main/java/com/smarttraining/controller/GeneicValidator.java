package com.smarttraining.controller;

import com.smarttraining.dto.DepositeLogDto;
import com.smarttraining.dto.TrainingAccountDto;
import com.smarttraining.dto.TrainingDto;
import com.smarttraining.dto.TrainingLogDto;
import com.smarttraining.entity.DepositeLog;
import com.smarttraining.entity.Training;
import com.smarttraining.entity.TrainingAccount;
import com.smarttraining.entity.TrainingLog;
import com.smarttraining.exception.BadRequestException;
import com.smarttraining.querymodel.BaseQueryModel;
import com.smarttraining.querymodel.UserQueryModel;
import com.smarttraining.util.Util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;

@Component
public abstract class  GeneicValidator {
    
    @Autowired
    Util util;
    
    
    protected TrainingLog verifyTrainingLog(TrainingLogDto log) throws BadRequestException {
        if(log.getTrainerId() == null || log.getTrainerId().longValue() <= 0) {
            throw new BadRequestException("The trainner id can not be null or zero"); 
        }
        if(log.getTraineeId() == null || log.getTraineeId().longValue() <= 0) {
            throw new BadRequestException("The trainee id can not be null or zero"); 
        }
        
        return util.geneicMapping(log, TrainingLog.class);
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
            throw new BadRequestException("The unit price of a traiing can not be negative");
        }
        
        if(trainingDto.getLimitation() <= 0) {
            throw new BadRequestException("The limitation of a traiing can not be negative");
        }
        
        if(trainingDto.getStartDate() == null || trainingDto.getStartDate().isBefore(LocalDate.now(ZoneId.systemDefault()))) {
            throw new BadRequestException("The start date  of a traiing must be set a date of today or future");
        }
        
        if(trainingDto.getEndDate() != null && trainingDto.getEndDate().isBefore(trainingDto.getStartDate())) {
            throw new BadRequestException("The end date  of a traiing must be after the start date");
        }
        
        return util.geneicMapping(trainingDto, Training.class);
    }
    
    protected void verifyUserQueryModel(UserQueryModel userQueryModel) throws BadRequestException {
        verifyPageAndSize(userQueryModel);
        
        if(userQueryModel.getCreateDate() != null 
                && userQueryModel.getCreateDate().getMinVal() != null 
                && userQueryModel.getCreateDate().getMaxVal() != null
                && userQueryModel.getCreateDate().getMinVal().isAfter(userQueryModel.getCreateDate().getMaxVal())) {
            throw new BadRequestException("The min value of the create date should less than the max one");
        }
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
    
    private void verifyPageAndSize(BaseQueryModel queryModel)
            throws BadRequestException {
        if (queryModel.getPage() <= 0) {
            throw new BadRequestException("The page index can not be negative or zero");
        }
        
        if(queryModel.getSize() <= 0) {
            throw new BadRequestException("The page size can not be negative or zero");
        }
    }
}
