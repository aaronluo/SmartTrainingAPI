package com.smarttraining.util;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smarttraining.dto.UserDto;
import com.smarttraining.entity.Role;
import com.smarttraining.entity.Training;
import com.smarttraining.entity.TrainingAccount;
import com.smarttraining.entity.User;
import com.smarttraining.entity.UserProperty;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@RunWith(SpringRunner.class)
public class UtilTest {

    @TestConfiguration
    static class UserServiceTestConfig {
        @Bean
        public Util util() {
            return new Util();
        }

        @Bean
        public ModelMapper modelMapper() {
            return new ModelMapper();
        }
    }

    @Autowired
    Util util;

    @Test
    public void testUserDtoToUser() {
        UserDto userDto = new UserDto();
        userDto.setUsername("aaronluo");
        userDto.setPassword("sword2$8");
        

        User user = util.userDtoToUser(userDto);

        assertThat(user.getUsername()).isEqualTo(userDto.getUsername());
        assertThat(user.getProperties()).isNotNull();
        assertThat(user.getTrainingAccounts()).isNotNull();
        assertThat(user.getRoles()).isNotNull();
    }

    @Test
    public void testUserToUserDto() throws JsonParseException, JsonMappingException, IOException {
        User user = new User();
        user.setId(12345L);
        user.setUsername("aaronluo");
        user.setPassword("sword2$8");
        user.setCreateDate(LocalDateTime.now(ZoneId.systemDefault()));

        UserProperty prop = new UserProperty();
        prop.setName("family_name");
        prop.setValue("罗");
        prop.setCreateDate(LocalDateTime.now(ZoneId.systemDefault()));
        user.addProperty(prop);

        Role role = new Role();
        role.setName(Role.RoleType.ADMIN);
        user.addRole(role);

        Training training = new Training();
        training.setTitle("1-1 training");
        training.setLimitation(100);
        training.setUnitPrice(new BigDecimal(100.00));
        training.setStartDate(LocalDate.now(ZoneId.systemDefault()));
        training.setEndDate(training.getStartDate().plusYears(1));
        
        TrainingAccount account = new TrainingAccount();
        account.setTraining(training);
        account.setUnitPrice(new BigDecimal(95.00));
        account.setValidBeginDate(training.getStartDate());
        account.setValidEndDate(training.getEndDate());
        
        user.addTrainingAccount(account);
        
        ObjectMapper mapper = new ObjectMapper();
        Object json = mapper.readValue(TestUtil.objToJson(util.userToUserDto(user)), Object.class);   
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json));
    }
}
