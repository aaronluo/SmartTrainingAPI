package com.smarttraining.dao;

import com.smarttraining.entity.User;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserDao extends BaseDao<User> {
   Optional<User> findByUsername(String username);
}
