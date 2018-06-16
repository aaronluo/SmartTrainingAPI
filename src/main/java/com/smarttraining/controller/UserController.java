package com.smarttraining.controller;

import com.smarttraining.dto.UserDto;
import com.smarttraining.dto.UserPropertyDto;
import com.smarttraining.dto.UserToken;
import com.smarttraining.entity.User;
import com.smarttraining.entity.UserProperty;
import com.smarttraining.exception.ApiError;
import com.smarttraining.exception.ApiException;
import com.smarttraining.exception.InvalidUserOrPasswordException;
import com.smarttraining.exception.InvalidUsernamePasswordExcpetion;
import com.smarttraining.exception.PropertyAlreadyExistingException;
import com.smarttraining.exception.UserAlreadyExistingException;
import com.smarttraining.exception.UserNotFoundException;
import com.smarttraining.service.UserService;
import com.smarttraining.util.Util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value="/users")
public class UserController {

    @Autowired
    UserService userService;
    
    @Autowired
    Util util;
    /**
     * 登录
     * @param userDto
     * @return
     * @throws ApiException
     */
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
    
    /**
     * 用户注册，这步仅仅用用户名和密码创建一个新用户
     * @param userDto
     * @return
     * @throws ApiException
     */
    @RequestMapping(value="/register", method = RequestMethod.POST,  produces = "application/json")
    public User register(@RequestBody UserDto userDto) throws ApiException {
        User user = util.userDtoToUser(userDto);
        try {
            user =  userService.register(user);
        } catch (UserAlreadyExistingException
                | InvalidUsernamePasswordExcpetion e) {
            throw new ApiException(new ApiError(HttpStatus.BAD_REQUEST, e.getMessage()), e);
        }
        
        return user;
    }
    
    @RequestMapping(value="/users/{userId}/properties", method=RequestMethod.POST, produces = "application/json")
    public User addProperties(@PathVariable Long userId, 
            @RequestBody List<UserPropertyDto> properties) throws ApiException {
        List<UserProperty> props = new ArrayList<UserProperty>();
        properties.forEach(p -> props.add(util.propDtoToProp(p)));
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
        
        return user;
    }
}
