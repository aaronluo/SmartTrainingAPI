package com.smarttraining.security;

import com.smarttraining.dao.UserDao;
import com.smarttraining.entity.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class SecurityUserService implements UserDetailsService  {

    @Autowired
    UserDao userDao;
    
    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        User user = userDao.findByUsername(username).orElse(null);
        
        if(null == user) {
            throw new UsernameNotFoundException(String.format("The user[%s] is not found.", username));
        }
        
        return new SecurityUser(user.getUsername(), user.getPassword());
    }

}
