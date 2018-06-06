package com.smarttraining.dao;

import com.smarttraining.entity.User;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserDao extends BaseDao<User> {
    
//    @Query("select u from User u where u.active=true and u.username=:username")
    Optional<User> findByUsername(@Param("username") String username);
}
