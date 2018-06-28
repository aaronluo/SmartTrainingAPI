package com.smarttraining.security;

import com.smarttraining.dao.UserDao;
import com.smarttraining.entity.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
        
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(r -> new SimpleGrantedAuthority(r.getName())).collect(Collectors.toList());
        
        return new SecurityUser(user.getUsername(), user.getPassword(), authorities);
    }

}
