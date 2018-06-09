package com.smarttraining.dao;

import com.smarttraining.entity.BaseEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface BaseDao<T extends BaseEntity> extends JpaRepository<T, Long>, QueryDslPredicateExecutor<T> {
    
    @Query("select t from #{#entityName} t  order by t.id desc")
    List<T> findAll();
    
    Optional<T> findById(Long id);
//    
//    @Query("select t from #{#entityName} t where t.active=true and t.id=:id")
//    T getOne(Long id);
//    
    @Query("select t from #{#entityName} t order by t.id desc")
    Page<T> findAll(Pageable pageable);
    
//    @Query("update #{#entityName} t set t.active=false where t=:entity")
//    @Modifying
//    void delete(@Param("entity") T entity);
//    
//    @Query("update #{#entityName} t set t.active=false where t.id=?1")
//    @Modifying
//    void delete(Long id);
}
