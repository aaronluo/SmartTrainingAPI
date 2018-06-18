package com.smarttraining.controller;

import com.smarttraining.dto.TrainingAccountDto;
import com.smarttraining.dto.TrainingDto;
import com.smarttraining.entity.Training;
import com.smarttraining.entity.TrainingAccount;
import com.smarttraining.exception.QueryModelException;
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
    
    protected Training verifyTraining(TrainingDto trainingDto) throws QueryModelException {
        if(util.isEmpty(trainingDto.getTitle())){
            throw new QueryModelException("The title of a training can not be empty");
        }
        
        if(trainingDto.getUnitPrice().compareTo(BigDecimal.ZERO) == -1) {
            throw new QueryModelException("The unit price of a traiing can not be negative");
        }
        
        if(trainingDto.getLimitation() <= 0) {
            throw new QueryModelException("The limitation of a traiing can not be negative");
        }
        
        if(trainingDto.getStartDate() == null || trainingDto.getStartDate().isBefore(LocalDate.now(ZoneId.systemDefault()))) {
            throw new QueryModelException("The start date  of a traiing must be set a date of today or future");
        }
        
        if(trainingDto.getEndDate() != null && trainingDto.getEndDate().isBefore(trainingDto.getStartDate())) {
            throw new QueryModelException("The end date  of a traiing must be after the start date");
        }
        
        return util.geneicMapping(trainingDto, Training.class);
    }
    
    protected void verifyUserQueryModel(UserQueryModel userQueryModel) throws QueryModelException {
        verifyPageAndSize(userQueryModel);
        
        if(userQueryModel.getCreateDate() != null 
                && userQueryModel.getCreateDate().getMinVal() != null 
                && userQueryModel.getCreateDate().getMaxVal() != null
                && userQueryModel.getCreateDate().getMinVal().isAfter(userQueryModel.getCreateDate().getMaxVal())) {
            throw new QueryModelException("The min value of the create date should less than the max one");
        }
    }

    /**
     * verify if passed in accountDto has correct information for create a new account entity
     * @param accountDto
     * @return
     * @throws QueryModelException
     */
    protected TrainingAccount verifyAccount(TrainingAccountDto accountDto) throws QueryModelException {
        TrainingAccount account = util.trainingAccountDtoToTrainingAccount(accountDto);        
        if(account.getUnitPrice().compareTo(BigDecimal.ZERO) == -1) {
            throw new QueryModelException("The unit price can not be negative");
        }
        
        if(account.getBalance().compareTo(BigDecimal.ZERO) == -1) {
            throw new QueryModelException("The balance can not be negative");
        }
        
        if(account.getValidBeginDate().isAfter(account.getValidEndDate())) {
            throw new QueryModelException("The start date must be before the end date");
        }
        
        if(account.getTraining().getId() <=0 ) {
            throw new QueryModelException(String.format("The select training[%d] is invalid", account.getTraining().getId()));
        }
        
        return account;
    }
    
    private void verifyPageAndSize(BaseQueryModel queryModel)
            throws QueryModelException {
        if (queryModel.getPage() <= 0) {
            throw new QueryModelException("The page index can not be negative or zero");
        }
        
        if(queryModel.getSize() <= 0) {
            throw new QueryModelException("The page size can not be negative or zero");
        }
    }
}
