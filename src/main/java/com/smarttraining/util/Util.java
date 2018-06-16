package com.smarttraining.util;

import com.smarttraining.dto.UserDto;
import com.smarttraining.dto.UserPropertyDto;
import com.smarttraining.entity.User;
import com.smarttraining.entity.UserProperty;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public final class Util {
    
    @Value("${app.username:[a-zA-Z]{1}[a-zA-Z0-9_]{6,12}$}")
    protected String usernameRegx;
    
    @Value("${app.password:^(?![A-Z]+$)(?![a-z]+$)(?!\\d+$)(?![\\W_]+$)\\S{8,12}$}")
    protected String passwordRegx;
    
    @Autowired
    protected ModelMapper modelMapper;
    
    public boolean  isValidUsername(String username) {
        return isEmpty(username) ? false: username.matches(usernameRegx);
    };
    
    public  boolean isValidPassword(String password) {
        return isEmpty(password) ? false : password.matches(passwordRegx);
    }
    
    public boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }
    
    public User userDtoToUser(UserDto userDto) {

       modelMapper.addMappings(new PropertyMap<UserDto, User>(){
           @Override
           public void configure() {
               skip().setProperties(null);
               skip().setTrainingAccounts(null);
//               skip().setRoles(null);
           }
       });
        
        return modelMapper.map(userDto, User.class);
    }
    
    public UserDto userToUserDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }

    public UserProperty propDtoToProp(UserPropertyDto propDto) {
        return modelMapper.map(propDto, UserProperty.class);
    }
}
