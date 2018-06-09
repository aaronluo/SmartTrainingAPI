package com.smarttraining.dao;

import com.smarttraining.entity.Training;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrainingDao extends BaseDao<Training> {

//    @Query("select t from Training t where t.title like %?1%")
    List<Training> findByTitleLike(String title);
}
