package com.smarttraining.service;

import com.smarttraining.dao.UserDao;
import com.smarttraining.dto.UserToken;
import com.smarttraining.entity.User;
import com.smarttraining.exception.InvalidUserOrPasswordException;
import com.smarttraining.exception.UserNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private  UserDao userDao;
    
    public boolean isUserExisting(String username) {
        Optional<User> user = userDao.findByUsername(username);
        
        return user.isPresent();
    }
    
    /**
     * 
     * @param username
     * @param password - must be MD5 encoded
     * @return
     * @throws UserNotFoundException 
     * @throws InvalidUserOrPasswordException 
     */
    public UserToken login(String username, String password) 
            throws UserNotFoundException, InvalidUserOrPasswordException {
        Optional<User> user = userDao.findByUsername(username);
        UserToken token = new UserToken();
        
        if(user.isPresent()) {
            if(user.get().getPassword().equals(password)){
                //TODO: generate user token 
            }else{
                throw new InvalidUserOrPasswordException(username);
            }
        }else{
            throw new UserNotFoundException(username);
        }
        
        return token;
    }
    
}
