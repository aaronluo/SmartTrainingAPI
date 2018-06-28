package com.smarttraining.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.smarttraining.dao.DepositeLogDao;
import com.smarttraining.dao.RoleDao;
import com.smarttraining.dao.TrainingDao;
import com.smarttraining.dao.TrainingLogDao;
import com.smarttraining.dao.UserDao;
import com.smarttraining.entity.DepositeLog;
import com.smarttraining.entity.QDepositeLog;
import com.smarttraining.entity.QRole;
import com.smarttraining.entity.QTraining;
import com.smarttraining.entity.QTrainingAccount;
import com.smarttraining.entity.QTrainingLog;
import com.smarttraining.entity.QUser;
import com.smarttraining.entity.Role;
import com.smarttraining.entity.Training;
import com.smarttraining.entity.TrainingAccount;
import com.smarttraining.entity.TrainingLog;
import com.smarttraining.entity.User;
import com.smarttraining.entity.UserProperty;
import com.smarttraining.exception.BadRequestException;
import com.smarttraining.exception.InvalidUsernamePasswordExcpetion;
import com.smarttraining.exception.PropertyAlreadyExistingException;
import com.smarttraining.exception.TrainingAccountAlreadyExistingExecption;
import com.smarttraining.exception.TrainingAccountNotFoundException;
import com.smarttraining.exception.TrainingNotFoundException;
import com.smarttraining.exception.UserAlreadyExistingException;
import com.smarttraining.exception.UserNotFoundException;
import com.smarttraining.querymodel.BaseQueryModel;
import com.smarttraining.querymodel.DepositeQueryModel;
import com.smarttraining.querymodel.TrainingLogQueryModel;
import com.smarttraining.querymodel.TrainingQueryModel;
import com.smarttraining.querymodel.UserQueryModel;
import com.smarttraining.util.Util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
    private DepositeLogDao depositeLogDao;
    
    @Autowired
    private TrainingLogDao trainingLogDao;
    
    @Autowired
    private Util util;
    
//    @Autowired
//    private JWTUtil jwtUtil;
    
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
//    public UserToken login(String username, String password) 
//            throws UserNotFoundException, InvalidUserOrPasswordException {
//        Optional<User> user = userDao.findByUsername(username);
//        UserToken token = new UserToken();
//        
//        if(user.isPresent()) {
//            if(passwordEncoder.matches(password, user.get().getPassword())){
//                token.setUsername(username);
//                token.setUserId(user.get().getId());
//                token.setAccessToken(jwtUtil.composeJwtToken(user.get()));
//                token.setExpireDatetime(jwtUtil.parseExpirationDate(token.getAccessToken()));
//                token.setRefreshToken("12345");
//            }else{
//                throw new InvalidUserOrPasswordException(username);
//            }
//        }else{
//            throw new UserNotFoundException(username);
//        }
//        
//        return token;
//    }
    
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
     * 更新用户信息，前端需限制住这个方法仅用来更新用户密码
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
        User user = userDao.findOne(id);
        
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
            builder.and(user.username.like(userQueryModel.getUsername() + "%"));
        }
        if(userQueryModel.getCreateDate() != null) {
            if(userQueryModel.getCreateDate().getMinVal() != null) {
                builder.and(user.createDate.goe(userQueryModel.getCreateDate().getMinVal()));
            }
            if(userQueryModel.getCreateDate().getMaxVal() != null) {
                builder.and(user.createDate.loe(userQueryModel.getCreateDate().getMaxVal()));
            }
        }

        if(userQueryModel.getRoles() != null && userQueryModel.getRoles().size() > 0) {
            builder.and(user.roles.any().in(JPAExpressions.selectFrom(role).where(role.name.in(userQueryModel.getRoles()))));
        }
        return userDao.findAll(builder.getValue(), 
                cratePageRequest(userQueryModel));
    }

    /**
     * 给指定用户的指定培训账户充值
     * @param userId
     * @param accountId
     * @param deposite
     * @return
     * @throws UserNotFoundException
     * @throws TrainingAccountNotFoundException
     */
    public TrainingAccount deposite(Long userId, Long accountId, DepositeLog deposite) 
            throws UserNotFoundException, TrainingAccountNotFoundException {
        User user = this.getUser(userId);
        
        TrainingAccount account = user.getTrainingAccounts().stream()
                .filter(c -> c.getId() == accountId).findFirst().orElse(null);
        
        if(account == null) {
            throw new TrainingAccountNotFoundException("" + userId, accountId);
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
    public TrainingAccount updateTrainingAccount(Long userId,
            Long accountId, TrainingAccount account) 
                    throws UserNotFoundException, TrainingAccountNotFoundException {
        /*
         * 仅能更新培训账户的有效日期和培训单价。
         * 如果需要更新账户余额，必须进行充值操作；
         * 如果要更改账户对应的培训类型，必须进行新建账户操作，将账户
         * 余额迁移至新账户
         */
        User user = getUser(userId);
        TrainingAccount curAccount = user.getTrainingAccounts().stream()
                .filter(c -> c.getId() == accountId).findFirst().orElse(null);
        
        if(curAccount == null) {
            throw new TrainingAccountNotFoundException("" + userId, accountId);
        }
        
        curAccount.setValidBeginDate(account.getValidBeginDate());
        curAccount.setValidEndDate(account.getValidEndDate());
        curAccount.setUnitPrice(account.getUnitPrice());
        
        userDao.saveAndFlush(user);
        
        return curAccount;
    }

    /**
     * 查询用户存钱的记录
     * @param userId
     * @param accountId
     * @param query
     * @return
     */
    public Page<DepositeLog> listUserDepositeLog(Long userId, Long accountId,
            DepositeQueryModel query) {
        QDepositeLog depositeLog = QDepositeLog.depositeLog;
        QTrainingAccount account = QTrainingAccount.trainingAccount;
        QUser user = QUser.user;
        
        BooleanBuilder builder = new BooleanBuilder();
        
        builder
            .and(depositeLog.account.id.eq(accountId))
            .and(depositeLog.account.owner.id.eq(userId));
        
        if(query.getAmount() != null) {
            if(query.getAmount().getMinVal() != null) {
                builder.and(depositeLog.amount.goe(query.getAmount().getMinVal()));
            }
            
            if(query.getAmount().getMaxVal() != null) {
                builder.and(depositeLog.amount.loe(query.getAmount().getMaxVal()));
            }
        }
        
        if(query.getCreateDate() != null) {
            if(query.getCreateDate().getMinVal() != null) {
                builder.and(depositeLog.createDate.goe(query.getCreateDate().getMinVal()));
            }
            if(query.getCreateDate().getMaxVal() != null) {
                builder.and(depositeLog.createDate.loe(query.getCreateDate().getMaxVal()));
            }
        }
        
        return depositeLogDao.findAll(builder.getValue(), cratePageRequest(query));
    }

    public Page<Training> listInvolvedTrainings(Long userId,
            TrainingQueryModel query) throws UserNotFoundException, BadRequestException {
        QUser user = QUser.user;
        QTraining training = QTraining.training;
        QTrainingAccount account = QTrainingAccount.trainingAccount;
        QTrainingLog log = QTrainingLog.trainingLog;
        BooleanBuilder builder = new BooleanBuilder();
        
        if(!util.isEmpty(query.getTitle())) {
            builder.and(training.title.like(query.getTitle() + "%"));
        }
        
        User pUser = userDao.findOne(userId);
        if(pUser == null) {
             throw new UserNotFoundException("" + userId);
        }
        
        if(pUser.getRoles().stream().filter(r -> r.getName().equals("ROLE_TRAINEE")).count() > 0) {
            //This user is a trainee
            builder.and(training.accounts.any().owner.id.eq(userId));
        }else if(pUser.getRoles().stream().filter(r -> r.getName().equals("ROLE_TRAINER")).count() > 0) {
            //this user is a trainner
            builder.and(training.accounts.any().trainingLogs.any().trainer.id.eq(userId));
        }else{
            throw new BadRequestException(String.format("This user[%d] is not a trainner or trainee", userId));
        }

        return trainingDao.findAll(builder.getValue(), cratePageRequest(query));
    }

    public Page<TrainingLog> listTrainingLog(Long userId, Long accountId,
            TrainingLogQueryModel query) {
        
        QTrainingLog trainingLog = QTrainingLog.trainingLog;
        QTrainingAccount account = QTrainingAccount.trainingAccount;
        QUser user = QUser.user;
        
        BooleanBuilder builder = new BooleanBuilder();
        
        builder.and(trainingLog.trainee.id.eq(userId)).and(trainingLog.account.id.eq(accountId));
        
        if(query.getTrainerId() != null) {
            builder.and(trainingLog.trainer.id.eq(query.getTrainerId()));
        }
        
        if(query.getCreateDate() != null) {
            if(query.getCreateDate().getMinVal() != null) {
                builder.and(trainingLog.createDate.goe(query.getCreateDate().getMinVal()));
            }
            if(query.getCreateDate().getMaxVal() != null) {
                builder.and(trainingLog.createDate.loe(query.getCreateDate().getMaxVal()));
            }
        }
        
        
        
        return trainingLogDao.findAll(builder.getValue(), cratePageRequest(query));
        
    }
    
    private PageRequest cratePageRequest(BaseQueryModel query) {
        return new PageRequest(query.getPage() - 1, query.getSize(), Sort.Direction.DESC, "id");
    }
}
