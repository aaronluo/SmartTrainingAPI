package com.smarttraining.util;

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
        UserDto ret = modelMapper.map(user, UserDto.class);
        
        user.getTrainingAccounts().forEach(c -> {
            ret.getTrainingAccounts().stream().filter(a -> a.getId() == c.getId())
                .findFirst().ifPresent( a -> {
                    a.setDepositeLogCount(c.getDepositLogs().size());
                    a.setTraingLogCount(c.getTrainingLogs().size());
                });
        });
        
        ret.setPassword("********");
        
        return ret;
    }

    public UserProperty propDtoToProp(UserPropertyDto propDto) {
        return modelMapper.map(propDto, UserProperty.class);
    }

    public TrainingAccount trainingAccountDtoToTrainingAccount(
            TrainingAccountDto accountDto) {
        return modelMapper.map(accountDto, TrainingAccount.class);
    }
    
    public TrainingAccountDto trainingAccountToDto(TrainingAccount account) {
        TrainingAccountDto ret = this.geneicMapping(account, TrainingAccountDto.class);
        
        ret.setDepositeLogCount(account.getDepositLogs().size());
        ret.setTraingLogCount(account.getTrainingLogs().size());
        
        return ret;
    }

    public DepositeLogDto deplositeLogtoDto(DepositeLog log) {
        return this.geneicMapping(log, DepositeLogDto.class);
    }
    
    public TrainingDto trainingToDto(Training training) {
        return this.geneicMapping(training, TrainingDto.class);
    }
    
    public <D, T> D geneicMapping(T source, Class<D> destClass) {
        return modelMapper.map(source, destClass);
    }

    public TrainingLog trainingLogDtoToPo(TrainingLogDto log) {
        return modelMapper.map(log, TrainingLog.class);
    }
    
    public TrainingLogDto trainingLogToDto(TrainingLog log) {
        return modelMapper.map(log, TrainingLogDto.class);
    }
    
//    public <D, T> T dtoToPo(D dto, Class<T> typeParameterClass) {
//        return modelMapper.map(dto, typeParameterClass);
//    }
}
