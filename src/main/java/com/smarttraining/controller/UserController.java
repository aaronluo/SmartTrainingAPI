package com.smarttraining.controller;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import com.smarttraining.dto.DepositeLogDto;
import com.smarttraining.dto.TrainingAccountDto;
import com.smarttraining.dto.TrainingDto;
import com.smarttraining.dto.TrainingLogDto;
import com.smarttraining.dto.UserDto;
import com.smarttraining.dto.UserPropertyDto;
import com.smarttraining.entity.DepositeLog;
import com.smarttraining.entity.Training;
import com.smarttraining.entity.TrainingAccount;
import com.smarttraining.entity.TrainingLog;
import com.smarttraining.entity.User;
import com.smarttraining.entity.UserProperty;
import com.smarttraining.exception.ApiException;
import com.smarttraining.exception.BadRequestException;
import com.smarttraining.exception.InvalidUsernamePasswordExcpetion;
import com.smarttraining.exception.PropertyAlreadyExistingException;
import com.smarttraining.exception.TrainingAccountAlreadyExistingExecption;
import com.smarttraining.exception.TrainingAccountNotFoundException;
import com.smarttraining.exception.TrainingNotFoundException;
import com.smarttraining.exception.UserAlreadyExistingException;
import com.smarttraining.exception.UserNotFoundException;
import com.smarttraining.querymodel.DepositeQueryModel;
import com.smarttraining.querymodel.TrainingLogQueryModel;
import com.smarttraining.querymodel.TrainingQueryModel;
import com.smarttraining.querymodel.UserQueryModel;
import com.smarttraining.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;


@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value="/api/users")
public class UserController extends GeneicValidator {

    @Autowired
    UserService userService;
    
    @ApiOperation(value="register a new user with specified roles")
    @ApiResponses(value={
             @ApiResponse(code=200, message="newly created user", response=UserDto.class),
             @ApiResponse(code=403, message="Access forbidden"),
             @ApiResponse(code=400, message="Wrong username or password")
     })
    @RequestMapping(value="/register", method = POST,  produces = "application/json")
    public UserDto register(@RequestBody UserDto userDto) throws ApiException {
        User user = util.geneicMapping(userDto, User.class);
        try {
            user =  userService.register(user);
        } catch (UserAlreadyExistingException
                | InvalidUsernamePasswordExcpetion e) {
            handleException(e);
        }
        
        return util.userToUserDto(user);
    }
    
    @ApiOperation(value="add addtional property to a sepcific user")
    @ApiResponses(value={
             @ApiResponse(code=200, message="newly created user", response=UserDto.class),
             @ApiResponse(code=403, message="Access forbidden"),
             @ApiResponse(code=400, message="Inccorect property name or value"),
             @ApiResponse(code=404, message="User not found")
     })
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value="/{userId}/properties", method=POST, produces = "application/json")
    public UserDto  addProperties(@PathVariable Long userId, 
            @RequestBody List<UserPropertyDto> properties) throws ApiException {
        List<UserProperty> props = new ArrayList<UserProperty>();
        properties.forEach(p -> props.add(util.propDtoToProp(p)));
        
        if(props.stream().filter(p -> util.isEmpty(p.getName())).count() > 0) {
            handleException(BAD_REQUEST, "Property name cannot be empty");
        }
        User user = null;
        try {
            user = userService.addProperties(userId, props);
        } catch (UserNotFoundException | PropertyAlreadyExistingException e) {
            handleException(e);
        } 
        
        return util.userToUserDto(user);
    }
    
    
    @ApiOperation(value="update addtional property to a sepcific user")
    @ApiResponses(value={
             @ApiResponse(code=200, message="newly created user", response=UserDto.class),
             @ApiResponse(code=403, message="Access forbidden"),
             @ApiResponse(code=400, message="Inccorect property name or value"),
             @ApiResponse(code=404, message="User or property not found")
     })
    @RequestMapping(value="/{userId}/properties", method=PUT, produces = "application/json")
    public UserDto updateProperties(@PathVariable Long userId, 
            @RequestBody List<UserPropertyDto> properties) throws ApiException {
        //TODO:
        return null;
    }
    
    @ApiOperation(value="add a new training account for a specifc user")
    @ApiResponses(value={
             @ApiResponse(code=200, message="updated user inform with the newly add account", response=UserDto.class),
             @ApiResponse(code=403, message="Access forbidden"),
             @ApiResponse(code=400, message="Incorrect account info or the account already there"),
             @ApiResponse(code=404, message="User not found or the selected training not found")
     })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value="/{userId}/accounts", method=POST, produces="application/json")
    public UserDto addTrainingAccount(@PathVariable Long userId, 
            @RequestBody TrainingAccountDto accountDto) throws ApiException {
        
        User user = null;
        try{
            TrainingAccount account = verifyAccount(accountDto);
            user = userService.addAccount(userId, account);
        }catch(UserNotFoundException | TrainingNotFoundException | BadRequestException | TrainingAccountAlreadyExistingExecption e) {
            handleException(e);
        }
        
        return util.userToUserDto(user);
    }
    
    @ApiOperation(value="get a user by username")
    @ApiResponses(value={
             @ApiResponse(code=200, message="the user info including roles, accounts and properties", response=UserDto.class),
             @ApiResponse(code=403, message="Access forbidden"),
             @ApiResponse(code=404, message="User not found or the selected training not found")
     })
    @RequestMapping(value="/{userId}", method=GET, produces="application/json")
    public UserDto getUser(@PathVariable Long  userId) throws ApiException {

        User user = null;
        try {
            user = userService.getUser(userId);
        } catch (UserNotFoundException e) {
            handleException(e);
        }
        
        return  util.userToUserDto(user);
    }
    
    @ApiOperation(value="query users by page")
    @ApiResponses(value={
             @ApiResponse(code=200, message="Current page of users"),
             @ApiResponse(code=403, message="Access forbidden"),
             @ApiResponse(code=400, message="Bad Request")
     })
    @RequestMapping(value="/query", method=POST, produces="application/json")
    public Page<UserDto> listUser(@RequestBody UserQueryModel userQueryModel) throws ApiException {
        Page<User> users = null;
        try {
            verifyUserQueryModel(userQueryModel);
            users = userService.listUser(userQueryModel);
        } catch (BadRequestException e) {
            handleException(e);
        }
        
        return users.map(util::userToUserDto);
    }
    
    @ApiOperation(value="Deposite money into a traiing account of a specific user")
    @ApiResponses(value={
             @ApiResponse(code=200, message="Updated training account"),
             @ApiResponse(code=403, message="Access forbidden"),
             @ApiResponse(code=400, message="Bad Request"),
             @ApiResponse(code=404, message="The user or the training account not found")
     })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value="/{userId}/accounts/{accountId}/deposite", method=POST, produces = "application/json")
    public TrainingAccountDto addMoney(@PathVariable Long userId, 
            @PathVariable Long accountId, @RequestBody DepositeLogDto depositeDto) throws ApiException {
        TrainingAccount updatedAccount = null;
         try {
            DepositeLog depostie = verifyDepositeLog(depositeDto);
            updatedAccount = userService.deposite(userId, accountId, depostie);
        } catch (BadRequestException | TrainingAccountNotFoundException | UserNotFoundException e) {
            handleException(e);
        }
         
         return util.trainingAccountToDto(updatedAccount);
    }
    
    
    @RequestMapping(value="/{userId}/accounts/{accountId}/deposite/query", 
            method=POST, produces="application/json")
    public Page<DepositeLogDto> listDepositeLog(@PathVariable Long userId, 
            @PathVariable Long accountId, @RequestBody DepositeQueryModel query) throws ApiException{
        Page<DepositeLog> logs = null;
        
        try {
            verifyDepositeQueryModel(query);
            
            logs = userService.listUserDepositeLog(userId, accountId, query);
        } catch (BadRequestException e) {
            handleException(e);
        }
        
        return logs.map(util::deplositeLogtoDto);
    }

    @ApiOperation(value="update an account of a specific user")
    @ApiResponses(value={
             @ApiResponse(code=200, message="Current page of users"),
             @ApiResponse(code=403, message="Access forbidden"),
             @ApiResponse(code=400, message="Bad Request"),
             @ApiResponse(code=404, message="The user or the training account not found")
     })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value="/{userId}/accounts/{accountId}", method=PUT, produces = "application/json")
    public TrainingAccountDto updateAccount(@PathVariable Long userId, 
            @PathVariable Long accountId, @RequestBody TrainingAccountDto accountDto) throws ApiException {
        
        TrainingAccount account = null;
        
        try {
            account = this.verifyAccount(accountDto);
            /*
             * 仅能更新培训账户的有效日期和培训单价。
             */
            account = userService.updateTrainingAccount(userId, accountId, account);
            
            
        } catch (BadRequestException|UserNotFoundException | TrainingAccountNotFoundException  e) {
            handleException(e);
        } 
        
        return util.geneicMapping(account, TrainingAccountDto.class);
    }
    
    @ApiOperation(value="query trainings being involved by a specific user")
    @ApiResponses(value={
             @ApiResponse(code=200, message="Current page of users"),
             @ApiResponse(code=403, message="Access forbidden"),
             @ApiResponse(code=400, message="Bad Request"),
             @ApiResponse(code=404, message="The user or the training account not found")
     })
    @RequestMapping(value="/{userId}/trainings/query", method=POST, produces="application/json")
    public Page<TrainingDto> listInvolvedTrainings(@PathVariable Long userId, 
            @RequestBody TrainingQueryModel query) throws ApiException {
        
        Page<Training> trainings = null;
        
        try {
            verifyTrainingQueryModel(query);
            
            //only use training title as query critiera
            trainings = userService.listInvolvedTrainings(userId, query);
            
        } catch (BadRequestException | UserNotFoundException e) {
            handleException(e);
        } 

        return trainings.map(util::trainingToDto);
    }

    @RequestMapping(value="/{userId}/accounts/{accountId}/trainingLogs/query", method=POST, produces="application/json")
    public Page<TrainingLogDto> listTrainingLog(@PathVariable Long userId,
            @PathVariable Long accountId, @RequestBody TrainingLogQueryModel query) throws ApiException {
        Page<TrainingLog> logs = null;
        try {
            verifyTrainingLogQueryModel(query);
            
            logs = userService.listTrainingLog(userId, accountId, query);
            
        } catch (BadRequestException e) {
            this.handleException(e);
        }
        
        return logs.map(util::trainingLogToDto);
    }
}
