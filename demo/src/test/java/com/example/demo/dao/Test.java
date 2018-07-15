package com.example.demo.dao;

import com.example.demo.configuration.Column;
import com.example.demo.configuration.Parse;
import com.example.demo.configuration.QueryRunner;
import com.example.demo.entity.User;
import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.BeanProcessor;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.jta.TransactionFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.sql.DataSource;
import javax.transaction.Transactional;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.List;
import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
@Transactional
@Rollback(value = false)
public class Test {
    @Autowired
    private DataSource dataSource;
    @Autowired
    private EntityManager entityManager;
    @org.junit.Test
    public void test(){
        User user=new User();
        user.setId(1L);
        user.setUsername("zhangsan");
        user.setPassword("admin");
        entityManager.persist(user);
    }
    @org.junit.Test
    public void query() throws SQLException {
        QueryRunner queryRunner=new QueryRunner(dataSource);
        org.apache.commons.dbutils.QueryRunner queryRunner1=new org.apache.commons.dbutils.QueryRunner();
        String sql="select id as id ,username as username from user";
        Long stringObjectMap = queryRunner.getNumber(sql,"id");
        System.out.println(stringObjectMap);
    }
    @org.junit.Test
    public void parseing(){
        QueryRunner queryRunner=new QueryRunner(dataSource);
        User user = new User();
        user.setPassword("adimin");
        user.setDate(new Date());
        user.setDate1(new java.sql.Date(System.currentTimeMillis()));
        user.setABoolean(true);
        System.out.println(user);
        StringBuilder stringBuilder = queryRunner.fillStatementWithBean(user);
        System.out.println(stringBuilder);


    }
}
