package com.smarttraining.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.smarttraining.dao.RoleDao;
import com.smarttraining.dao.TrainingDao;
import com.smarttraining.dao.UserDao;
import com.smarttraining.dto.UserToken;
import com.smarttraining.entity.QRole;
import com.smarttraining.entity.QUser;
import com.smarttraining.entity.Role;
import com.smarttraining.entity.Training;
import com.smarttraining.entity.TrainingAccount;
import com.smarttraining.entity.User;
import com.smarttraining.entity.UserProperty;
import com.smarttraining.exception.InvalidUserOrPasswordException;
import com.smarttraining.exception.InvalidUsernamePasswordExcpetion;
import com.smarttraining.exception.PropertyAlreadyExistingException;
import com.smarttraining.exception.TrainingAccountAlreadyExistingExecption;
import com.smarttraining.exception.TrainingNotFoundException;
import com.smarttraining.exception.UserAlreadyExistingException;
import com.smarttraining.exception.UserNotFoundException;
import com.smarttraining.querymodel.UserQueryModel;
import com.smarttraining.util.JWTUtil;
import com.smarttraining.util.Util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private  UserDao userDao;
    
    @Autowired
    private RoleDao roleDao;
    
    @Autowired
    private TrainingDao trainingDao;
    
    @Autowired
    private Util util;
    
    @Autowired
    private JWTUtil jwtUtil;
    
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
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
            if(passwordEncoder.matches(password, user.get().getPassword())){
                token.setUsername(username);
                token.setUserId(user.get().getId());
                token.setAccessToken(jwtUtil.composeJwtToken(user.get()));
                token.setExpireDatetime(jwtUtil.parseExpirationDate(token.getAccessToken()));
                token.setRefreshToken("12345");
            }else{
                throw new InvalidUserOrPasswordException(username);
            }
        }else{
            throw new UserNotFoundException(username);
        }
        
        return token;
    }
    
    public User register(User user) 
            throws UserAlreadyExistingException, InvalidUsernamePasswordExcpetion {
        if(isUserExisting(user.getUsername())) {
            throw new UserAlreadyExistingException(user.getUsername());
        }
        
        if(util.isValidUsername(user.getUsername()) && 
                util.isValidPassword(user.getPassword())) {
            //密码加密
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            //set user role cause the passed in role is not a persist entity
            List<Role> roles = new ArrayList<Role>();
            user.getRoles().forEach(r -> {
                Optional<Role> role = roleDao.findByName(r.getName());
                if(role.isPresent()) {
                    roles.add(role.get());
                }
            });
            
            user.setRoles(roles);
            
            user = userDao.saveAndFlush(user);
        }else{
            throw new InvalidUsernamePasswordExcpetion(user.getUsername(), user.getPassword());
        }
        
        return user;
    }
    
    public User updateUser(User user) {
        return userDao.saveAndFlush(user);
    }
    
    public User getUser(String username) throws UserNotFoundException {
        Optional<User> user =  userDao.findByUsername(username);
        if(user.isPresent()) {
            return user.get();
        }else{
            throw new UserNotFoundException(username);
        }
    }
    
    public User getUser(Long id) throws UserNotFoundException {
        User user = userDao.getOne(id);
        
        if (user == null)
            throw  new UserNotFoundException("" + id);
        else
            return user;
    }

    public User addProperties(Long userId, List<UserProperty> props)
            throws PropertyAlreadyExistingException, UserNotFoundException {

        User user = getUser(userId);
        
        if(user.getProperties().size() == 0) {
           props.forEach(p -> user.addProperty(p));
        }else {
            for (UserProperty prop : user.getProperties()) {
                for (UserProperty p : props) {
                    if (prop.getName().equals(p.getName())) {
                        throw new PropertyAlreadyExistingException(
                                user.getUsername(), prop.getName());
                    } else {
                        user.addProperty(p);
                    }
                }
            }
        }
        
        return userDao.saveAndFlush(user);
    }

    public User addAccount(Long userId, TrainingAccount account) 
            throws UserNotFoundException, TrainingNotFoundException, TrainingAccountAlreadyExistingExecption {

        User user = getUser(userId);
        Training training = trainingDao.findOne(account.getTraining().getId());
        
        if(null == training) {
            throw new TrainingNotFoundException(account.getTraining().getId());
        }else if(user.getTrainingAccounts().stream().filter(c -> c.getTraining().getId() == training.getId()).count() > 0){
            throw new TrainingAccountAlreadyExistingExecption(user.getUsername(), training.getTitle());
        }else{
            account.setTraining(training);
            user.addTrainingAccount(account);
            user = userDao.saveAndFlush(user);
        }
        
        return user;
    }

    public Page<User> listUser(UserQueryModel userQueryModel) {
        QUser user = QUser.user;
        QRole role = QRole.role;
        BooleanBuilder builder = new BooleanBuilder();
        
        if(!util.isEmpty(userQueryModel.getUsername())) {
            builder.and(user.username.like( "%" + userQueryModel.getUsername() + "%"));
        }
        if(userQueryModel.getCreateDate() != null) {
            if(userQueryModel.getCreateDate().getMinVal() != null) {
                builder.and(user.createDate.gt(userQueryModel.getCreateDate().getMinVal()));
            }
            if(userQueryModel.getCreateDate().getMaxVal() != null) {
                builder.and(user.createDate.lt(userQueryModel.getCreateDate().getMaxVal()));
            }
        }

        if(userQueryModel.getRoles() != null && userQueryModel.getRoles().size() > 0) {
            builder.and(user.roles.contains(JPAExpressions.selectFrom(role).where(role.name.in("TRAINER"))));
        }
        return userDao.findAll(builder.getValue(), 
                new PageRequest(userQueryModel.getPage() - 1, userQueryModel.getSize()));
    }
}
