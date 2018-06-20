package com.smarttraining.controller;

import com.smarttraining.dto.DepositeLogDto;
import com.smarttraining.dto.TrainingAccountDto;
import com.smarttraining.dto.UserDto;
import com.smarttraining.dto.UserPropertyDto;
import com.smarttraining.dto.UserToken;
import com.smarttraining.entity.DepositeLog;
import com.smarttraining.entity.TrainingAccount;
import com.smarttraining.entity.User;
import com.smarttraining.entity.UserProperty;
import com.smarttraining.exception.ApiError;
import com.smarttraining.exception.ApiException;
import com.smarttraining.exception.InvalidUserOrPasswordException;
import com.smarttraining.exception.InvalidUsernamePasswordExcpetion;
import com.smarttraining.exception.PropertyAlreadyExistingException;
import com.smarttraining.exception.BadRequestException;
import com.smarttraining.exception.TrainingAccountAlreadyExistingExecption;
import com.smarttraining.exception.TrainingAccountNotFoundException;
import com.smarttraining.exception.TrainingNotFoundException;
import com.smarttraining.exception.UserAlreadyExistingException;
import com.smarttraining.exception.UserNotFoundException;
import com.smarttraining.querymodel.UserQueryModel;
import com.smarttraining.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value="/users")
public class UserController extends GeneicValidator {

    @Autowired
    UserService userService;
    
    @ApiOperation(value="user login endpoint")
    @ApiResponses(value={
             @ApiResponse(code=200, message="user access token JWT", response=UserToken.class),
             @ApiResponse(code=403, message="Access forbidden"),
             @ApiResponse(code=400, message="Wrong username or password")
     })
    @RequestMapping(value="/login", method = RequestMethod.POST,  produces = "application/json")
    public UserToken login(@RequestBody UserDto userDto) throws ApiException {
        UserToken token = null;
        try {
            token = userService.login(userDto.getUsername(), userDto.getPassword());
        } catch (UserNotFoundException | InvalidUserOrPasswordException e) {
            throw new ApiException(new ApiError(HttpStatus.BAD_REQUEST, e.getMessage()), e);
        }
        
        return token;
    }
    
    @ApiOperation(value="register a new user with specified roles")
    @ApiResponses(value={
             @ApiResponse(code=200, message="newly created user", response=UserDto.class),
             @ApiResponse(code=403, message="Access forbidden"),
             @ApiResponse(code=400, message="Wrong username or password")
     })
    @RequestMapping(value="/register", method = RequestMethod.POST,  produces = "application/json")
    public UserDto register(@RequestBody UserDto userDto) throws ApiException {
        User user = util.userDtoToUser(userDto);
        try {
            user =  userService.register(user);
        } catch (UserAlreadyExistingException
                | InvalidUsernamePasswordExcpetion e) {
            throw new ApiException(new ApiError(HttpStatus.BAD_REQUEST, e.getMessage()), e);
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
    @RequestMapping(value="/{userId}/properties", method=RequestMethod.POST, produces = "application/json")
    public UserDto  addProperties(@PathVariable Long userId, 
            @RequestBody List<UserPropertyDto> properties) throws ApiException {
        List<UserProperty> props = new ArrayList<UserProperty>();
        properties.forEach(p -> props.add(util.propDtoToProp(p)));
        
        if(props.stream().filter(p -> util.isEmpty(p.getName())).count() > 0) {
            throw new ApiException(
                    new ApiError(HttpStatus.BAD_REQUEST, "Property name cannot be empty"),  null);
        }
        
        User user = null;
        
        try {
            user = userService.addProperties(userId, props);
        } catch (UserNotFoundException e) {
            throw new ApiException(
                    new ApiError(HttpStatus.NOT_FOUND, e.getMessage()), e);
        } catch (PropertyAlreadyExistingException e) {
            throw new ApiException(
                    new ApiError(HttpStatus.BAD_REQUEST, e.getMessage()), e);
        }
        
        return util.userToUserDto(user);
    }
    
    @ApiOperation(value="add a new training account for a specifc user")
    @ApiResponses(value={
             @ApiResponse(code=200, message="updated user inform with the newly add account", response=UserDto.class),
             @ApiResponse(code=403, message="Access forbidden"),
             @ApiResponse(code=400, message="Incorrect account info or the account already there"),
             @ApiResponse(code=404, message="User not found or the selected training not found")
     })
    @RequestMapping(value="/{userId}/accounts", method=RequestMethod.POST, produces="application/json")
    public UserDto addTrainingAccount(@PathVariable Long userId, 
            @RequestBody TrainingAccountDto accountDto) throws ApiException {
        
        User user = null;
        try{
            TrainingAccount account = verifyAccount(accountDto);
            user = userService.addAccount(userId, account);
        }catch(UserNotFoundException | TrainingNotFoundException e) {
            throw new ApiException(
                    new ApiError(HttpStatus.NOT_FOUND, e.getMessage()), e);
        }catch(BadRequestException | TrainingAccountAlreadyExistingExecption  e) {
            throw new ApiException(
                    new ApiError(HttpStatus.BAD_REQUEST, e.getMessage()), e);
        }
        
        return util.userToUserDto(user);
    }
    
    @ApiOperation(value="get a user by username")
    @ApiResponses(value={
             @ApiResponse(code=200, message="the user info including roles, accounts and properties", response=UserDto.class),
             @ApiResponse(code=403, message="Access forbidden"),
             @ApiResponse(code=404, message="User not found or the selected training not found")
     })
    @RequestMapping(value="/{username}", method=RequestMethod.GET, produces="application/json")
    public UserDto getUser(@PathVariable String username) throws ApiException {
        User user = null;
        try {
            user = userService.getUser(username);
        } catch (UserNotFoundException e) {
            throw new ApiException(new ApiError(HttpStatus.NOT_FOUND, e.getMessage()), e);
        }
        
        return util.userToUserDto(user);
    }
    
    @ApiOperation(value="query users by page")
    @ApiResponses(value={
             @ApiResponse(code=200, message="Current page of users"),
             @ApiResponse(code=403, message="Access forbidden"),
             @ApiResponse(code=400, message="Bad Request")
     })
    @RequestMapping(value="/query", method=RequestMethod.POST, produces="application/json")
    public Page<UserDto> listUser(@RequestBody UserQueryModel userQueryModel) throws ApiException {
        Page<User> users = null;
        
        try {
            verifyUserQueryModel(userQueryModel);
            users = userService.listUser(userQueryModel);
        } catch (BadRequestException e) {
            throw new ApiException(new ApiError(HttpStatus.BAD_REQUEST, e.getMessage()), e);
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
    @RequestMapping(value="/{username}/accounts/{accountId}/deposite", method=RequestMethod.POST, produces = "application/json")
    public TrainingAccountDto addMoney(@PathVariable String username, 
            @PathVariable Long accountId, @RequestBody DepositeLogDto depositeDto) throws ApiException {
         try {
            DepositeLog depostie = verifyDepositeLog(depositeDto);
            TrainingAccount updatedAccount = userService.deposite(username, accountId, depostie);
            
            return util.geneicMapping(updatedAccount, TrainingAccountDto.class);
        } catch (BadRequestException e) {
            throw new ApiException(new ApiError(HttpStatus.BAD_REQUEST, e.getMessage()), e);
        } catch (UserNotFoundException | TrainingAccountNotFoundException e) {
            throw new ApiException(new ApiError(HttpStatus.NOT_FOUND, e.getMessage()), e);
        }
    }
    
    @ApiOperation(value="query users by page")
    @ApiResponses(value={
             @ApiResponse(code=200, message="Current page of users"),
             @ApiResponse(code=403, message="Access forbidden"),
             @ApiResponse(code=400, message="Bad Request"),
             @ApiResponse(code=404, message="The user or the training account not found")
     })
    @RequestMapping(value="/{username}/accounts/{accountId}", method=RequestMethod.PUT, produces = "application/json")
    public TrainingAccountDto updateAccount(@PathVariable String username, 
            @PathVariable Long accountId, @RequestBody TrainingAccountDto accountDto) throws ApiException {
        try {
            TrainingAccount account = this.verifyAccount(accountDto);
            account = userService.updateTrainingAccount(username, accountId, account);
            
            return util.geneicMapping(account, TrainingAccountDto.class);
        } catch (BadRequestException  e) {
            throw new ApiException(new ApiError(HttpStatus.BAD_REQUEST, e.getMessage()), e);
        } catch (UserNotFoundException | TrainingAccountNotFoundException  e) {
            throw new ApiException(new ApiError(HttpStatus.NOT_FOUND, e.getMessage()), e);
        }
    }
}
