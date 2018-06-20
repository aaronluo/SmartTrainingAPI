package com.smarttraining.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.smarttraining.dao.RoleDao;
import com.smarttraining.dao.TrainingDao;
import com.smarttraining.dao.UserDao;
import com.smarttraining.dto.UserToken;
import com.smarttraining.entity.DepositeLog;
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
import com.smarttraining.exception.TrainingAccountNotFoundException;
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
    
    /**
     * 坚持指定用户名用户是否存在
     * @param username
     * @return
     */
    public boolean isUserExisting(String username) {
        Optional<User> user = userDao.findByUsername(username);
        
        return user.isPresent();
    }
    
    /**
     * 用户登录，返回access_key, 用户名，用户组等相关信息
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
    
    /**
     * 用户注册，仅设置用户名，密码和权限，附加属性，训练账户信息调用其他方法添加
     * @param user
     * @return
     * @throws UserAlreadyExistingException
     * @throws InvalidUsernamePasswordExcpetion
     */
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
    
    /**
     * 更新用户信息，前端需限制住次方法仅用来更新用户密码
     * @param user
     * @return
     */
    public User updateUser(User user) {
        return userDao.saveAndFlush(user);
    }
    
    /**
     * 根据用户获取用户信息
     * @param username
     * @return
     * @throws UserNotFoundException
     */
    public User getUser(String username) throws UserNotFoundException {
        Optional<User> user =  userDao.findByUsername(username);
        if(user.isPresent()) {
            return user.get();
        }else{
            throw new UserNotFoundException(username);
        }
    }
    
    /**
     * 根据用户id获取用户信息
     * @param id
     * @return
     * @throws UserNotFoundException
     */
    public User getUser(Long id) throws UserNotFoundException {
        User user = userDao.getOne(id);
        
        if (user == null)
            throw  new UserNotFoundException("" + id);
        else
            return user;
    }

    /**
     * 为指定用户添加附加属性
     * @param userId
     * @param props
     * @return
     * @throws PropertyAlreadyExistingException
     * @throws UserNotFoundException
     */
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
    /**
     * 为指定 用户添加训练账户
     * @param userId
     * @param account
     * @return
     * @throws UserNotFoundException
     * @throws TrainingNotFoundException
     * @throws TrainingAccountAlreadyExistingExecption
     */
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

    /**
     * 分页获取用户列表，可以使用用户名，用户创建时间和用户角色查询
     * @param userQueryModel
     * @return
     */
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

    /**
     * 给指定用户的指定培训账户充值
     * @param username
     * @param accountId
     * @param deposite
     * @return
     * @throws UserNotFoundException
     * @throws TrainingAccountNotFoundException
     */
    public TrainingAccount deposite(String username, Long accountId, DepositeLog deposite) 
            throws UserNotFoundException, TrainingAccountNotFoundException {
        User user = this.getUser(username);
        
        TrainingAccount account = user.getTrainingAccounts().stream()
                .filter(c -> c.getId() == accountId).findFirst().orElse(null);
        
        if(account == null) {
            throw new TrainingAccountNotFoundException(username, accountId);
        }
        
        account.AddDepositeLog(deposite);
        
        userDao.saveAndFlush(user);
        
        return account;
    }

    /**
     * 更新指定用户指定账户的基本信息，仅包括有效日期和培训单价
     * @param username
     * @param accountId
     * @param account
     * @return
     * @throws UserNotFoundException
     * @throws TrainingAccountNotFoundException 
     */
    public TrainingAccount updateTrainingAccount(String username,
            Long accountId, TrainingAccount account) 
                    throws UserNotFoundException, TrainingAccountNotFoundException {
        /*
         * 仅能更新培训账户的有效日期和培训单价。
         * 如果需要更新账户余额，必须进行充值操作；
         * 如果要更改账户对应的培训类型，必须进行新建账户操作，将账户
         * 余额迁移至新账户
         */
        User user = getUser(username);
        TrainingAccount curAccount = user.getTrainingAccounts().stream()
                .filter(c -> c.getId() == accountId).findFirst().orElse(null);
        
        if(curAccount == null) {
            throw new TrainingAccountNotFoundException(username, accountId);
        }
        
        curAccount.setValidBeginDate(account.getValidBeginDate());
        curAccount.setValidEndDate(account.getValidEndDate());
        curAccount.setUnitPrice(account.getUnitPrice());
        
        userDao.saveAndFlush(user);
        
        return curAccount;
    }
}
